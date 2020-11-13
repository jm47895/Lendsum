package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.adapter.MessageListAdapter
import com.lendsumapp.lendsum.adapter.UserSearchListAdapter
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentMessagesBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_BUNDLE_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_REQUEST_KEY
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesFragment : Fragment(), View.OnClickListener,
    UserSearchListAdapter.Interaction, MessageListAdapter.Interaction,
    SearchView.OnQueryTextListener{

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val messagesViewModel: MessagesViewModel by viewModels()
    private lateinit var userSearchListAdapter: UserSearchListAdapter
    private lateinit var messageListAdapter: MessageListAdapter
    private lateinit var remoteDbUserListObserver: Observer<List<User>>
    private var currentListOfMessages = listOf<Message>()
    private var guestUser = User()
    private var hostUser = User()
    private var isChatRoomEmpty = true
    private var currentChatRoom: ChatRoom? = null
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messagesViewModel.getCurrentCachedUser().observe(viewLifecycleOwner, Observer { cachedUser->
            hostUser = cachedUser
        })

        binding.chatRoomSendMsgBtn.setOnClickListener(this)
        binding.chatRoomBackBtn.setOnClickListener(this)
        binding.chatRoomSearchView.setOnQueryTextListener(this)

        //This listener listens for click from the chat room list in the messages fragment and populates the data associated with the list
        setFragmentResultListener(CHAT_ROOM_REQUEST_KEY){ _, bundle->

            currentChatRoom = bundle.getParcelable(CHAT_ROOM_BUNDLE_KEY)

            setCacheMessageObserver(currentChatRoom?.chatRoomId.toString())
            messagesViewModel.registerMessagesSyncListener(currentChatRoom?.chatRoomId.toString())

            initRecyclerView(MESSAGE_RECYCLER_VIEW)


            val users = currentChatRoom?.participants!!
            handleMessageUi(users)

            binding.chatRoomList.visibility = View.VISIBLE

            isChatRoomEmpty = false
        }

        if(isChatRoomEmpty) {
            initRecyclerView(SEARCH_RECYCLER_VIEW)
        }

        remoteDbUserListObserver = Observer { userList->

            userSearchListAdapter.submitList(userList)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.chatRoomList.adapter = null
        _binding = null
        clearFragmentResultListener(CHAT_ROOM_REQUEST_KEY)
        messagesViewModel.getRemoteDbUserList().removeObserver(remoteDbUserListObserver)
        messagesViewModel.unregisterMessagesSyncListener(currentChatRoom?.chatRoomId.toString())
    }

    private fun initRecyclerView(recyclerViewType: Int){

        when(recyclerViewType){
            SEARCH_RECYCLER_VIEW->{

                userSearchListAdapter = UserSearchListAdapter(this@MessagesFragment)
                binding.chatRoomList.layoutManager = LinearLayoutManager(context)
                binding.chatRoomList.adapter = userSearchListAdapter

            }
            MESSAGE_RECYCLER_VIEW->{

                messageListAdapter = MessageListAdapter(this@MessagesFragment)

                binding.chatRoomList.layoutManager = LinearLayoutManager(context).apply {
                    stackFromEnd = true
                }
                binding.chatRoomList.adapter = messageListAdapter
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.chat_room_back_btn->{
                findNavController().navigate(R.id.action_chatRoomFragment_to_messagesFragment)
            }
            R.id.chat_room_send_msg_btn->{

                if (binding.chatRoomRecipientTv.isVisible) {

                    val msg = binding.chatRoomMsgEt.text.toString()

                    if (currentListOfMessages.isEmpty()) {

                        createNewChatRoom(msg)

                    } else {
                        currentChatRoom?.let { addNewMessage(msg, it) }
                    }
                }else{
                    AndroidUtils.hideKeyboard(requireActivity())
                    AndroidUtils.showSnackBar(requireActivity(), getString(R.string.pick_user_first))
                    binding.chatRoomMsgEt.text?.clear()
                }

            }
        }
    }

    private fun setCacheMessageObserver(chatId: String){
        messagesViewModel.getCurrentCachedMessages(chatId).observe(viewLifecycleOwner, Observer {
            Log.d(TAG, it.toString())
            currentListOfMessages = it
            messageListAdapter.submitList(it)
            binding.chatRoomList.scrollToPosition(it.size - 1)

            messagesViewModel.getNumberOfRealtimeMessages().observe(viewLifecycleOwner, Observer { listOfRealtimeMessages->

                if(currentListOfMessages.size < listOfRealtimeMessages.size){
                    messagesViewModel.syncMessageData(chatId)
                }
            })
        })
    }

    private fun addNewMessage(msg: String, chatRoom: ChatRoom) {

        //TODO Eventually allow offline edits. Currently it is coded to only show in the UI offline
        if(NetworkUtils.isNetworkAvailable(requireContext())){
            val newMessage = Message(AndroidUtils.getTimestampInstant(), chatRoom.chatRoomId, firebaseAuth.currentUser?.uid.toString(), guestUser.profilePicUri, msg, null)
            binding.chatRoomMsgEt.text?.clear()

            cacheNewMessage(newMessage)
            sendMessageToRealtimeDB(chatRoom.chatRoomId, newMessage)
            updateCachedChatRoom(chatRoom, msg)
            updateRealTimeDbChatRoom(chatRoom)
        }else{
            AndroidUtils.showSnackBar(requireActivity(), "You must be connected to the internet to do this.")
        }
    }

    private fun updateRealTimeDbChatRoom(chatRoom: ChatRoom) {
        messagesViewModel.updateChatRoomInRealTimeDb(chatRoom)
    }

    private fun sendMessageToRealtimeDB(chatRoomId: String, msg: Message) {
        messagesViewModel.addMessageToRealTimeDb(chatRoomId, msg)
    }

    private fun cacheNewMessage(newMessage: Message) {
        messagesViewModel.cacheNewMessage(newMessage)
    }

    private fun updateCachedChatRoom(chatRoom: ChatRoom, lastMsg: String) {
        chatRoom.lastMessage = lastMsg
        chatRoom.lastTimestamp = AndroidUtils.getTimestampInstant()
        messagesViewModel.updateLocalCachedChatRoom(chatRoom)
    }

    private fun createNewChatRoom(msg: String) {
        val guestId = guestUser.userId
        val hostId = hostUser.userId
        val idList = listOf(guestId, hostId).sorted()
        val chatRoomId = idList[0].substring(0, 5) + idList[1].substring(0, 5)
        val chatRoomUserList = listOf(guestUser)

        val newChatRoom = ChatRoom(chatRoomId, chatRoomUserList, msg, AndroidUtils.getTimestampInstant())

        messagesViewModel.addChatRoomObjectToRealTimeDb(chatRoomId, newChatRoom)
        messagesViewModel.addChatRoomIdToRealTimeDb(idList, chatRoomId)
        messagesViewModel.cacheNewChatRoom(newChatRoom)

        addNewMessage(msg, newChatRoom)

        setCacheMessageObserver(chatRoomId)

        currentChatRoom = newChatRoom
    }

    override fun onUserItemSelected(position: Int, item: User) {

        guestUser = item

        val newParticipant = listOf(item)

        initRecyclerView(MESSAGE_RECYCLER_VIEW)
        handleMessageUi(newParticipant)

        binding.chatRoomList.visibility = View.VISIBLE

        Log.d(TAG, "We are at position $position")
    }

    private fun handleMessageUi(users: List<User>){

        AndroidUtils.hideKeyboard(requireActivity())
        binding.chatRoomList.visibility = View.INVISIBLE
        binding.chatRoomSearchView.visibility = View.GONE
        binding.chatRoomRecipientTv.visibility = View.VISIBLE

        setParticipants(users)

    }

    private fun setParticipants(users: List<User>){
        var participants = ""

        if(users.size > 1){
            for(user in users){
                participants += user.name + "/"
            }
        }else{
            participants = users[0].name
        }

        binding.chatRoomRecipientTv.text = participants
    }

    override fun onMessageItemSelected(position: Int, item: Message) {
        Log.d(TAG,"Message Item Clicked at: $position" )
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "Query text submitted.")
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        if(query.isBlank() || query.isEmpty()){
            userSearchListAdapter.submitList(emptyList())
            messagesViewModel.getRemoteDbUserList().removeObserver(remoteDbUserListObserver)
        }else{
            messagesViewModel.getRemoteDbUserList().observe(viewLifecycleOwner, remoteDbUserListObserver)
            messagesViewModel.findUserInRemoteDb(query)
        }

        Log.d(TAG, "Query text changed.")
        return false
    }

    companion object {
        private val TAG = MessagesFragment::class.simpleName
        private const val SEARCH_RECYCLER_VIEW = 0
        private const val MESSAGE_RECYCLER_VIEW = 1
    }

}
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
import com.lendsumapp.lendsum.databinding.FragmentChatRoomBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_BUNDLE_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_REQUEST_KEY
import com.lendsumapp.lendsum.viewmodel.ChatRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomFragment : Fragment(), View.OnClickListener,
    UserSearchListAdapter.Interaction, MessageListAdapter.Interaction,
    SearchView.OnQueryTextListener{

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding
    private val chatRoomViewModel: ChatRoomViewModel by viewModels()
    private lateinit var userSearchListAdapter: UserSearchListAdapter
    private lateinit var messageListAdapter: MessageListAdapter
    private lateinit var remoteDbUserListObserver: Observer<List<User>>
    private lateinit var cachedUserObserver: Observer<User>
    private lateinit var cachedMessagesObserver: Observer<List<Message>>
    private var currentListOfMessages = mutableListOf<Message>()
    private var guestUser = User()
    private var hostUser = User()
    private var isChatRoomEmpty = true
    private var currentChatRoom: ChatRoom? = null
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatRoomViewModel.getCurrentCachedUser(firebaseAuth.currentUser?.uid.toString())

        cachedUserObserver = Observer { cachedUser->
            hostUser = cachedUser
        }

        cachedMessagesObserver = Observer { cachedListOfMessages ->
            currentListOfMessages = cachedListOfMessages.toMutableList()
            messageListAdapter.submitList(currentListOfMessages)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRoomViewModel.getUser().observe(viewLifecycleOwner, cachedUserObserver)

        binding?.chatRoomSendMsgBtn?.setOnClickListener(this)
        binding?.chatRoomBackBtn?.setOnClickListener(this)
        binding?.chatRoomSearchView?.setOnQueryTextListener(this)

        //This listener listens for click from the chat room list in the messages fragment and populates the data associated with the list
        setFragmentResultListener(CHAT_ROOM_REQUEST_KEY){ key, bundle->

            currentChatRoom = bundle.getParcelable(CHAT_ROOM_BUNDLE_KEY)
            chatRoomViewModel.getCurrentCachedMessages(currentChatRoom?.chatRoomId.toString())

            initRecyclerView(MESSAGE_RECYCLER_VIEW)

            chatRoomViewModel.getCurrentMessages().observe(viewLifecycleOwner, cachedMessagesObserver)

            val users = currentChatRoom?.participants!!
            handleMessageUi(users)

            binding?.chatRoomList?.visibility = View.VISIBLE

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
        binding?.chatRoomList?.adapter = null
        _binding = null
        clearFragmentResultListener(CHAT_ROOM_REQUEST_KEY)
        chatRoomViewModel.getRemoteDbUserList().removeObserver(remoteDbUserListObserver)
    }

    private fun initRecyclerView(recyclerViewType: Int){

        when(recyclerViewType){
            SEARCH_RECYCLER_VIEW->{

                userSearchListAdapter = UserSearchListAdapter(this@ChatRoomFragment)
                binding?.chatRoomList?.layoutManager = LinearLayoutManager(context)
                binding?.chatRoomList?.adapter = userSearchListAdapter

            }
            MESSAGE_RECYCLER_VIEW->{

                messageListAdapter = MessageListAdapter(this@ChatRoomFragment)

                binding?.chatRoomList?.layoutManager = LinearLayoutManager(context).apply {
                    stackFromEnd = true
                }
                binding?.chatRoomList?.adapter = messageListAdapter
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.chat_room_back_btn->{
                //chatRoomViewModel.addMessagesToRealTimeDb(currentChatRoom?.chatRoomId.toString(), currentListOfMessages)
                findNavController().navigate(R.id.action_chatRoomFragment_to_messagesFragment)
            }
            R.id.chat_room_send_msg_btn->{

                if (binding?.chatRoomRecipientTv?.isVisible!!) {

                    val msg = binding?.chatRoomMsgEt?.text.toString()

                    if (currentListOfMessages.isEmpty()) {

                        createNewChatRoom(msg)

                    } else {
                        currentChatRoom?.let { addNewMessage(msg, it) }
                    }
                }else{
                    AndroidUtils.hideKeyboard(requireActivity())
                    AndroidUtils.showSnackBar(requireActivity(), getString(R.string.pick_user_first))
                    binding?.chatRoomMsgEt?.text?.clear()
                }

            }
        }
    }

    private fun addNewMessage(msg: String, chatRoom: ChatRoom) {

        val newMessage = Message(AndroidUtils.getTimestampInstant(), chatRoom.chatRoomId, firebaseAuth.currentUser?.uid.toString(), guestUser.profilePicUri, msg, null)

        currentListOfMessages.add(newMessage)
        messageListAdapter.submitList(currentListOfMessages.toMutableList())
        messageListAdapter.notifyDataSetChanged()
        binding?.chatRoomList?.smoothScrollToPosition(currentListOfMessages.size -1)
        binding?.chatRoomMsgEt?.text?.clear()

        cacheNewMessage(newMessage)
        updateCachedChatRoom(chatRoom, msg)

    }

    private fun cacheNewMessage(newMessage: Message) {
        chatRoomViewModel.cacheNewMessage(newMessage)
    }

    private fun updateCachedChatRoom(chatRoom: ChatRoom, lastMsg: String) {
        chatRoom.lastMessage = lastMsg
        chatRoom.lastTimestamp = AndroidUtils.getTimestampInstant()
        chatRoomViewModel.updateExistingCachedChatRoom(chatRoom)
    }

    private fun createNewChatRoom(msg: String) {
        val guestId = guestUser.userId
        val hostId = hostUser.userId
        val idList = listOf(guestId, hostId)
        Collections.sort(idList)
        val chatRoomId = idList[0].substring(0, 5) + idList[1].substring(0, 5)
        val chatRoomUserList = listOf(guestUser)

        val newChatRoom = ChatRoom(chatRoomId, chatRoomUserList, msg, AndroidUtils.getTimestampInstant())

        //chatRoomViewModel.addParticipantsToRealTimeDb(chatRoomId, listOf(guestUser, hostUser))
        //chatRoomViewModel.addChatroomUserToRealTimeDb(idList, chatRoomId)
        chatRoomViewModel.cacheNewChatRoom(newChatRoom)

        addNewMessage(msg, newChatRoom)

        currentChatRoom = newChatRoom
    }

    override fun onUserItemSelected(position: Int, item: User) {

        guestUser = item

        val newParticipant = listOf<User>(item)

        initRecyclerView(MESSAGE_RECYCLER_VIEW)
        handleMessageUi(newParticipant)

        binding?.chatRoomList?.visibility = View.VISIBLE

        Log.d(TAG, "We are at position $position")
    }

    private fun handleMessageUi(users: List<User>){

        organizeChatRoomWidgets()

        setParticipants(users)

    }

    private fun organizeChatRoomWidgets(){

        AndroidUtils.hideKeyboard(requireActivity())
        binding?.chatRoomList?.visibility = View.INVISIBLE
        binding?.chatRoomSearchView?.visibility = View.GONE
        binding?.chatRoomRecipientTv?.visibility = View.VISIBLE
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

        binding?.chatRoomRecipientTv?.text = participants
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
            chatRoomViewModel.getRemoteDbUserList().removeObserver(remoteDbUserListObserver)
        }else{
            chatRoomViewModel.getRemoteDbUserList().observe(viewLifecycleOwner, remoteDbUserListObserver)
            chatRoomViewModel.findUserInRemoteDb(query)
        }

        Log.d(TAG, "Query text changed.")
        return false
    }

    companion object {
        private val TAG = ChatRoomFragment::class.simpleName
        private const val SEARCH_RECYCLER_VIEW = 0
        private const val MESSAGE_RECYCLER_VIEW = 1
    }

}
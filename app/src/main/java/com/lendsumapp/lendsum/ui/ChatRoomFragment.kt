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
import com.lendsumapp.lendsum.viewmodel.ChatRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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
    private var currentListOfMessages = mutableListOf<Message>()
    private var guestUser = User()
    private var isChatRoomEmpty = true
    private var currentChatRoom: ChatRoom? = null
    private var oldDate: String = ""
    private val hostId = FirebaseAuth.getInstance().currentUser?.uid.toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.chatRoomSendMsgBtn?.setOnClickListener(this)
        binding?.chatRoomBackBtn?.setOnClickListener(this)
        binding?.chatRoomSearchView?.setOnQueryTextListener(this)

        setFragmentResultListener("chatRoomRequestKey"){ key, bundle->

            currentChatRoom = bundle.getParcelable("chatRoomBundleKey")

            initRecyclerView(MESSAGE_RECYCLER_VIEW)

            val users = currentChatRoom?.participants!!
            handleMessageUi(users)


            currentListOfMessages = currentChatRoom?.listOfMessages!!

            messageListAdapter.submitList(currentListOfMessages)

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
        binding?.chatRoomList?.adapter = null
        super.onDestroyView()
        clearFragmentResultListener("chatRoomRequestKey")
        chatRoomViewModel.getRemoteDbUserList().removeObserver(remoteDbUserListObserver)
        _binding = null
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

        var dateAndTime = AndroidUtils.getDateAndTime()

        if (oldDate == AndroidUtils.getDateAndTime()){
            dateAndTime = ""
        }

        currentListOfMessages.add(Message(dateAndTime, chatRoom.chatRoomId, hostId, guestUser.profilePicUri, msg, null))
        messageListAdapter.submitList(currentListOfMessages.toMutableList())
        messageListAdapter.notifyDataSetChanged()
        binding?.chatRoomMsgEt?.text?.clear()
        binding?.chatRoomList?.smoothScrollToPosition(currentListOfMessages.size -1)
        oldDate = AndroidUtils.getDateAndTime()

        updateCachedChatRoom(chatRoom, msg)

    }

    private fun updateCachedChatRoom(chatRoom: ChatRoom, lastMsg: String) {
        chatRoom.lastMessage = lastMsg
        chatRoom.listOfMessages = currentListOfMessages
        chatRoom.lastTimestamp = AndroidUtils.getShortDate()
        chatRoomViewModel.updateExistingCachedChatRoom(chatRoom)
    }

    private fun createNewChatRoom(msg: String) {
        val guestId = guestUser.userId
        val idList = listOf(guestId, hostId)
        Collections.sort(idList)
        val chatRoomId = idList[0].substring(0, 5) + idList[1].substring(0, 5)
        val chatRoomUserList = listOf(guestUser)

        val newChatRoom = ChatRoom(chatRoomId, chatRoomUserList, currentListOfMessages, msg, AndroidUtils.getShortDate())

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
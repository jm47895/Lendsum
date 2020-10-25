package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import java.text.DateFormat
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
    private var listOfMessages = mutableListOf<Message>()
    private var guestUser = User()
    private var currentChatRoom = ChatRoom()
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

        val previousChatRoom = arguments?.getParcelable<ChatRoom>("sdfgsdfg")

        if(previousChatRoom != null){

            currentChatRoom = previousChatRoom

            val users = previousChatRoom.participants!!

            initRecyclerView(MESSAGE_RECYCLER_VIEW)
            handleMessageUi(users)
            listOfMessages = previousChatRoom.listOfMessages!!

            messageListAdapter.submitList(listOfMessages.toMutableList())

        }else {
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

                    if (listOfMessages.isEmpty()) {

                        createNewChatRoom(msg)

                    } else {
                        addNewMessage(msg, currentChatRoom.chatRoomId)
                    }
                }else{
                    AndroidUtils.hideKeyboard(requireActivity())
                    AndroidUtils.showSnackBar(requireActivity(), getString(R.string.pick_user_first))
                    binding?.chatRoomMsgEt?.text?.clear()
                }

            }
        }
    }

    private fun addNewMessage(msg: String, chatRoomId: String) {
        listOfMessages.add(Message(AndroidUtils.getDateAndTime(), chatRoomId, hostId, guestUser.profilePicUri, msg, null))
        messageListAdapter.submitList(listOfMessages.toMutableList())
        binding?.chatRoomMsgEt?.text?.clear()
        binding?.chatRoomList?.smoothScrollToPosition(listOfMessages.size -1)

    }

    private fun createNewChatRoom(msg: String) {
        val guestId = guestUser.userId
        val idList = listOf<String>(guestId, hostId)
        Collections.sort(idList)
        val chatRoomId = idList[0].substring(0, 5) + idList[1].substring(0, 5)
        val chatRoomUserList = listOf(guestUser)

        addNewMessage(msg, chatRoomId)

        val newChatRoom = ChatRoom(chatRoomId, chatRoomUserList, listOfMessages, msg)

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
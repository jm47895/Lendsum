package com.lendsumapp.lendsum.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var listOfImageUriObserver: Observer<MutableList<String>>
    private var currentListOfMessages = listOf<Message>()
    private var guestUser = User()
    private var hostUser = User()
    private var isChatRoomEmpty = true
    private var currentChatRoom: ChatRoom? = null
    private var currentListOfImgUris: MutableList<String>? = null
    @Inject lateinit var firebaseAuth: FirebaseAuth

    private val registerGalleryActivityResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { messagesViewModel.addImageUriToList(it.toString()) }
    }

    private val registerTakePictureActivityResult = registerForActivityResult(ActivityResultContracts.TakePicture()){

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*messagesViewModel.getCurrentCachedUser().observe(viewLifecycleOwner, Observer { cachedUser->
            hostUser = cachedUser
        })*/

        binding.messagesOpenGalleryBtn.setOnClickListener(this)
        binding.messagesTakePhotoBtn.setOnClickListener(this)
        binding.messagesAddImageBtn.setOnClickListener(this)
        binding.messagesSendMsgBtn.setOnClickListener(this)
        binding.messagesBackBtn.setOnClickListener(this)
        binding.messagesUserSearchView.setOnQueryTextListener(this)

        //This listener listens for click from the chat room list in the messages fragment and populates the data associated with the list
        setFragmentResultListener(CHAT_ROOM_REQUEST_KEY){ _, bundle->

            currentChatRoom = bundle.getParcelable(CHAT_ROOM_BUNDLE_KEY)

            setCacheMessageObserver(currentChatRoom?.chatRoomId.toString())

            val uid = firebaseAuth.currentUser?.uid.toString()
            val users = currentChatRoom?.participants!!
            for(user in users){
                if(user.userId != uid){
                    guestUser = user
                }
            }

            initRecyclerView(MESSAGE_RECYCLER_VIEW)

            handleMessageUi(users)

            binding.chatMessageList.visibility = View.VISIBLE

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
        binding.chatMessageList.adapter = null
        _binding = null
        clearFragmentResultListener(CHAT_ROOM_REQUEST_KEY)
        messagesViewModel.getRemoteDbUserList().removeObserver(remoteDbUserListObserver)
        registerGalleryActivityResult.unregister()
    }

    private fun clearEditTextFocus(){
        binding.messagesSendMsgEt
    }

    private fun initRecyclerView(recyclerViewType: Int){

        when(recyclerViewType){
            SEARCH_RECYCLER_VIEW->{

                userSearchListAdapter = UserSearchListAdapter(this@MessagesFragment)
                binding.chatMessageList.layoutManager = LinearLayoutManager(context)
                binding.chatMessageList.adapter = userSearchListAdapter

            }
            MESSAGE_RECYCLER_VIEW->{

                messageListAdapter = MessageListAdapter(this@MessagesFragment, guestUser.profilePicUri!!)

                binding.chatMessageList.layoutManager = LinearLayoutManager(context).apply {
                    stackFromEnd = true
                }
                binding.chatMessageList.adapter = messageListAdapter
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.messages_back_btn->{
                clearEditTextFocus()
                findNavController().navigate(R.id.action_messagesFragment_to_chatRoomsFragment)
            }
            R.id.messages_send_msg_btn->{

                if (binding.messagesRecipientTv.isVisible) {

                    val msg = binding.messagesSendMsgEt.text.toString()

                    if (currentListOfMessages.isEmpty()) {

                        createNewChatRoom(msg)

                    } else {
                        currentChatRoom?.let { addNewMessage(msg, it) }
                    }
                }else{
                    AndroidUtils.hideKeyboard(requireActivity())
                    AndroidUtils.showSnackBar(requireActivity(), getString(R.string.pick_user_first))
                    binding.messagesSendMsgEt.text?.clear()
                }

            }
            R.id.messages_add_image_btn->{
                AndroidUtils.showView(binding.messagesImageMenu)
            }
            R.id.messages_take_photo_btn->{
                launchCamera()
            }
            R.id.messages_open_gallery_btn->{
                openGalleryForImage()
            }
        }
    }

    private fun launchCamera() {
        val imageUri: Uri? = null
        registerTakePictureActivityResult.launch(imageUri)
    }

    private fun openGalleryForImage() {
        registerGalleryActivityResult.launch("image/*")
    }

    private fun setCacheMessageObserver(chatId: String){
        messagesViewModel.getCurrentCachedMessages(chatId).observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "cache Message Observer hit")
            currentListOfMessages = it
            messageListAdapter.submitList(it)
            if(it.size > 1) {
                binding.chatMessageList.smoothScrollToPosition(it.size - 1)
            }
        })

        messagesViewModel.syncMessagesData(chatId)
    }

    private fun addNewMessage(msg: String, chatRoom: ChatRoom) {

        //TODO Eventually allow offline edits. Currently it is coded to only show in the UI when online
        if(NetworkUtils.isNetworkAvailable(requireContext())){
            val timeStamp = AndroidUtils.getTimestampInstant()
            val newMessage = Message(timeStamp, chatRoom.chatRoomId, firebaseAuth.currentUser?.uid.toString(), msg, currentListOfImgUris)
            binding.messagesSendMsgEt.text?.clear()

            currentListOfImgUris?.let {
                sendImgsToFirebaseStorage(chatRoom.chatRoomId, timeStamp, it)
            }

            cacheNewMessage(newMessage)
            sendMessageToRealtimeDB(chatRoom.chatRoomId, newMessage)
            updateCachedChatRoom(chatRoom, msg)
            updateRealTimeDbChatRoom(chatRoom)
        }else{
            AndroidUtils.showSnackBar(requireActivity(), "You must be connected to the internet to do this.")
        }
    }

    private fun sendImgsToFirebaseStorage(chatRoomId: String, timeStamp: Long, listOfImgUris: MutableList<String>
    ){
        messagesViewModel.uploadImgsToFirebaseStorage(chatRoomId, timeStamp, listOfImgUris)
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
        if(NetworkUtils.isNetworkAvailable(requireContext())){

            val chatRoomId = formChatId()
            val chatRoomUserList = listOf(guestUser, hostUser)
            val idList = listOf(guestUser.userId, hostUser.userId)

            val newChatRoom = ChatRoom(chatRoomId, chatRoomUserList, msg, AndroidUtils.getTimestampInstant())

            messagesViewModel.addChatRoomObjectToRealTimeDb(chatRoomId, newChatRoom)
            messagesViewModel.addChatRoomIdToRealTimeDb(idList, chatRoomId)
            messagesViewModel.cacheNewChatRoom(newChatRoom)

            addNewMessage(msg, newChatRoom)

            setCacheMessageObserver(chatRoomId)

            currentChatRoom = newChatRoom
        }else{
            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.not_connected_internet))
        }
    }

    private fun formChatId(): String{
        val guestId = guestUser.userId
        val hostId = hostUser.userId
        val sortedIdList = listOf(guestId, hostId).sorted()

        return sortedIdList[0].substring(0, 5) + sortedIdList[1].substring(0, 5)
    }

    override fun onUserItemSelected(position: Int, item: User) {

        guestUser = item

        initRecyclerView(MESSAGE_RECYCLER_VIEW)

        val chatRoomId = formChatId()
        messagesViewModel.getCurrentChatRoom(chatRoomId).observe(viewLifecycleOwner, Observer {
            currentChatRoom = it
        })
        setCacheMessageObserver(chatRoomId)

        val newParticipant = listOf(item)

        handleMessageUi(newParticipant)

        binding.chatMessageList.visibility = View.VISIBLE

        Log.d(TAG, "We are at position $position")
    }

    private fun handleMessageUi(users: List<User>){

        val currentUid = firebaseAuth.currentUser?.uid.toString()

        AndroidUtils.hideKeyboard(requireActivity())
        AndroidUtils.hideView(binding.chatMessageList)
        AndroidUtils.goneView(binding.messagesUserSearchView)
        AndroidUtils.showView(binding.messagesRecipientTv)

        for (user in users){
            if(user.userId != currentUid){
                binding.messagesRecipientTv.text = user.name
            }
        }

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
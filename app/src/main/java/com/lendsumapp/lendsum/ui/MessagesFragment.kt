package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.adapter.ChatRoomListAdapter
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.databinding.FragmentMessagesBinding
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_BUNDLE_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_REQUEST_KEY
import com.lendsumapp.lendsum.viewmodel.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment(), View.OnClickListener, ChatRoomListAdapter.Interaction {

    private var _binding:FragmentMessagesBinding? = null
    private val binding get() = _binding
    private val messagesViewModel: MessagesViewModel by viewModels()
    private lateinit var chatRoomsCacheObserver: Observer<List<ChatRoom>>
    private lateinit var chatRoomListAdapter: ChatRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messagesViewModel.getCachedChatRooms()

    }

    private fun initRecyclerView() {
        binding?.chatRoomList?.apply {
            chatRoomListAdapter = ChatRoomListAdapter(this@MessagesFragment)
            layoutManager = LinearLayoutManager(context)
            adapter = chatRoomListAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        binding?.messagesNewMessageBtn?.setOnClickListener(this)

        chatRoomsCacheObserver = Observer { chatRooms->

            loadChatRooms(chatRooms)

        }
        messagesViewModel.getChatRooms().observe(viewLifecycleOwner, chatRoomsCacheObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.chatRoomList?.adapter = null
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.messages_new_message_btn->{
                findNavController().navigate(R.id.action_messagesFragment_to_chatRoomFragment)
            }
        }
    }

    private fun loadChatRooms(chatRooms: List<ChatRoom>?) {

        if (chatRooms?.size == 0){
            binding?.messagesNoConversationsTv?.visibility = View.VISIBLE
        }else{
            binding?.messagesNoConversationsTv?.visibility = View.INVISIBLE
            chatRooms?.let { chatRoomListAdapter.submitList(it) }
        }


    }

    override fun onItemSelected(position: Int, item: ChatRoom) {
        setFragmentResult(CHAT_ROOM_REQUEST_KEY, bundleOf(CHAT_ROOM_BUNDLE_KEY to item))
        findNavController().navigate(R.id.action_messagesFragment_to_chatRoomFragment)
    }

    companion object{
        private val TAG = MessagesFragment::class.simpleName
    }
}
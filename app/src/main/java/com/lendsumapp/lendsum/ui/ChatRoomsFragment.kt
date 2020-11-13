package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.util.Log
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
import com.lendsumapp.lendsum.databinding.FragmentChatRoomsBinding
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_BUNDLE_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.CHAT_ROOM_REQUEST_KEY
import com.lendsumapp.lendsum.viewmodel.ChatRoomsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomsFragment : Fragment(), View.OnClickListener, ChatRoomListAdapter.Interaction {

    private var _binding:FragmentChatRoomsBinding? = null
    private val binding get() = _binding!!
    private val chatRoomsViewModel: ChatRoomsViewModel by viewModels()
    private lateinit var chatRoomListAdapter: ChatRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatRoomsViewModel.registerChatRoomSyncListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        binding.messagesNewMessageBtn.setOnClickListener(this)

        chatRoomsViewModel.getCachedChatRooms().observe(viewLifecycleOwner, Observer { currentCachedChatRooms->
            loadChatRooms(currentCachedChatRooms)
            chatRoomsViewModel.getNumberOfChatIdsFromRealtimeDb().observe(viewLifecycleOwner, Observer {
                if(currentCachedChatRooms.size < it.size){
                    if (currentCachedChatRooms.isEmpty()){
                        binding.messagesNoConversationsTv.visibility = View.INVISIBLE    
                    }
                    chatRoomsViewModel.syncChatRoomData(it[it.size-1])
                }
            })
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatRoomsViewModel.unregisterChatRoomSyncListener()
        binding.chatRoomList.adapter = null
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.messages_new_message_btn->{
                findNavController().navigate(R.id.action_messagesFragment_to_chatRoomFragment)
            }
        }
    }

    private fun initRecyclerView() {
        binding.chatRoomList.apply {
            chatRoomListAdapter = ChatRoomListAdapter(this@ChatRoomsFragment)
            layoutManager = LinearLayoutManager(context)
            adapter = chatRoomListAdapter
        }
    }

    private fun loadChatRooms(chatRooms: List<ChatRoom>?) {

        if (chatRooms?.size == 0){
            binding.messagesNoConversationsTv.visibility = View.VISIBLE
        }else{
            chatRooms?.let { chatRoomListAdapter.submitList(it) }
        }


    }

    override fun onItemSelected(position: Int, item: ChatRoom) {
        setFragmentResult(CHAT_ROOM_REQUEST_KEY, bundleOf(CHAT_ROOM_BUNDLE_KEY to item))
        findNavController().navigate(R.id.action_messagesFragment_to_chatRoomFragment)
    }

    companion object{
        private val TAG = ChatRoomsFragment::class.simpleName
    }
}
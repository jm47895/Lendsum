package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.adapter.UserSearchListAdapter
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentChatRoomBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.viewmodel.ChatRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomFragment : Fragment(), View.OnClickListener,
    UserSearchListAdapter.Interaction, SearchView.OnQueryTextListener{

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding
    private val chatRoomViewModel: ChatRoomViewModel by viewModels()
    @Inject lateinit var androidUtils: AndroidUtils
    private lateinit var userSearchListAdapter: UserSearchListAdapter
    private lateinit var remoteDbUserListObserver: Observer<List<User>>
    private var user = User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.chatRoomBackBtn?.setOnClickListener(this)
        binding?.chatRoomSearchView?.setOnQueryTextListener(this)

        initRecyclerView()

        remoteDbUserListObserver = Observer { userList->

            userSearchListAdapter.submitList(userList)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(){
        binding?.chatRoomList?.apply {
            layoutManager = LinearLayoutManager(activity)
            userSearchListAdapter = UserSearchListAdapter(this@ChatRoomFragment)
            adapter = userSearchListAdapter
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.chat_room_back_btn->{
                findNavController().navigate(R.id.action_chatRoomFragment_to_messagesFragment)
            }
        }
    }

    override fun onItemSelected(position: Int, item: User) {
        context?.let { androidUtils.hideKeyboard(it, requireView()) }
        binding?.chatRoomList?.visibility = View.INVISIBLE
        binding?.chatRoomSearchView?.visibility = View.GONE
        binding?.atSymbol?.visibility = View.INVISIBLE
        binding?.chatRoomRecipientTv?.visibility = View.VISIBLE
        user = item
        binding?.chatRoomRecipientTv?.text = user.name


        Log.d(TAG, "We are at position $position")
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
    }
}
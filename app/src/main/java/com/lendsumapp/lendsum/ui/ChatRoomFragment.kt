package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentChatRoomBinding
import com.lendsumapp.lendsum.databinding.FragmentMessagesBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding
    @Inject lateinit var androidUtils: AndroidUtils

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.chat_room_back_btn->{
                findNavController().navigate(R.id.action_chatRoomFragment_to_messagesFragment)
            }
        }
    }

    companion object {
        private val TAG = ChatRoomFragment::class.simpleName
    }

}
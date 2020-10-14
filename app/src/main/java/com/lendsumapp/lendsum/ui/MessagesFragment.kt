package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentMessagesBinding
import com.lendsumapp.lendsum.databinding.FragmentNumberVerificationBinding
import com.lendsumapp.lendsum.viewmodel.MessagesViewModel
import com.lendsumapp.lendsum.viewmodel.NumberVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment(), View.OnClickListener {

    private var _binding:FragmentMessagesBinding? = null
    private val binding get() = _binding
    private val messagesViewModel: MessagesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.messagesNewMessageBtn?.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.messages_new_message_btn->{
                findNavController().navigate(R.id.action_messagesFragment_to_chatRoomFragment)
            }
        }
    }

    companion object{
        private val TAG = MessagesFragment::class.simpleName
    }
}
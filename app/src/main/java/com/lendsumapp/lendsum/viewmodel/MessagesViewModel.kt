package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.lendsumapp.lendsum.ui.MessagesFragment

class MessagesViewModel @ViewModelInject constructor(

): ViewModel(){

    companion object{
        private val TAG = MessagesViewModel::class.simpleName
    }
}
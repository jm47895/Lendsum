package com.lendsumapp.lendsum.ui

import android.app.Application
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.room.Room
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.bundle.BundleDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    @Inject lateinit var dao: BundleDao

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}
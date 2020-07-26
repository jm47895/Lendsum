package com.lendsumapp.lendsum.ui

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.bundle.BundleDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    @Inject lateinit var dao: BundleDao

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bundle: com.lendsumapp.lendsum.data.bundle.Bundle = com.lendsumapp.lendsum.data.bundle.Bundle(
            0,
            "Jordan",
            "Lesly",
            "This is a bundle title",
            "This is a description",
            null,
            1234567,
            null,
            null,
            null,
            "days",
            null,
            null,
            null,
            false,
            true
        )

        CoroutineScope(IO).launch {
            dao.insertBundle(bundle)
        }

    }

}
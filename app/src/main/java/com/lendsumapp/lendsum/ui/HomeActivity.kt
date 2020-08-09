package com.lendsumapp.lendsum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lendsumapp.lendsum.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpBottomNavBarOnStart()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> {
                item.isChecked = true
                Toast.makeText(this, "profile!", Toast.LENGTH_SHORT).show()
            }
            R.id.messages -> {
                item.isChecked = true
                Toast.makeText(this, "messages!", Toast.LENGTH_SHORT).show()
            }
            R.id.search -> {
                item.isChecked = true
                Toast.makeText(this, "search!", Toast.LENGTH_SHORT).show()
            }
            R.id.bundles -> {
                item.isChecked = true
                Toast.makeText(this, "bundles!", Toast.LENGTH_SHORT).show()
            }
            R.id.services -> {
                item.isChecked = true
                Toast.makeText(this, "services!", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun setUpBottomNavBarOnStart(){
        navigation.selectedItemId = R.id.search
        navigation.setOnNavigationItemSelectedListener(this)
    }

}
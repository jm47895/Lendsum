package com.lendsumapp.lendsum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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

        navigation.setOnNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> Toast.makeText(this, "Profile!", Toast.LENGTH_SHORT).show()
            R.id.messages -> Toast.makeText(this, "Messages!", Toast.LENGTH_SHORT).show()
            R.id.add -> Toast.makeText(this, "Add!", Toast.LENGTH_SHORT).show()
            R.id.bundles -> Toast.makeText(this, "Bundles!", Toast.LENGTH_SHORT).show()
            R.id.services -> Toast.makeText(this, "Services!", Toast.LENGTH_SHORT).show()
        }
        return false
    }
}
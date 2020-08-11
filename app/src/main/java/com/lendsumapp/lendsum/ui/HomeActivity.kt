package com.lendsumapp.lendsum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lendsumapp.lendsum.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener{

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNavigation()

    }

    private fun setupBottomNavigation() {

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.marketplace

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottom_navigation, navHostFragment.navController)

    }

    fun showBottomNavigation()
    {
        bottom_navigation.visibility = View.VISIBLE
    }

    fun hideBottomNavigation()
    {
        bottom_navigation.visibility = View.GONE
    }

    private var backPressedOnce = false

    override fun onBackPressed() {
        if (navController.graph.startDestination == navController.currentDestination?.id)
        {
            if (backPressedOnce)
            {
                super.onBackPressed()
                return
            }

            backPressedOnce = true
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(2000){
                backPressedOnce = false
            }
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                item.isChecked = true
                print("P")
            }
            R.id.messages -> {
                item.isChecked = true
                print("Me")
            }
            R.id.marketplace -> {
                item.isChecked = true
                print("Ma")
            }
            R.id.bundles -> {
                item.isChecked = true
                print("B")
            }
            R.id.services -> {
                item.isChecked = true
                print("S")
            }
        }

        return false
    }

}
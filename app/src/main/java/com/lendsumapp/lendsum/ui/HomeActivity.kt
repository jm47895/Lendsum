package com.lendsumapp.lendsum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lendsumapp.lendsum.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener{

    private lateinit var navController: NavController
    private lateinit var menuItemStack: Stack<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNavigation()

    }

    private fun setupBottomNavigation() {
        menuItemStack = Stack()
        hideBottomNavigation()
        bottom_navigation.menu.setGroupCheckable(R.id.home_group, false, true)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener(this)

    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.profile -> {
                item.isCheckable = true
                item.isChecked = true
                menuItemStack.push(item)
                navController.navigate(R.id.profileFragment)
            }
            R.id.messages -> {
                item.isCheckable = true
                item.isChecked = true
                menuItemStack.push(item)
                navController.navigate(R.id.messagesFragment)
            }
            R.id.marketplace -> {
                item.isCheckable = true
                item.isChecked = true
                menuItemStack.push(item)
                navController.navigate(R.id.marketplaceFragment)

            }
            R.id.bundles -> {
                item.isCheckable = true
                item.isChecked = true
                menuItemStack.push(item)
                navController.navigate(R.id.bundlesFragment)

            }
            R.id.services -> {
                item.isCheckable = true
                item.isChecked = true
                menuItemStack.push(item)
                navController.navigate(R.id.servicesFragment)

            }
        }


        Log.d("HomeActivity", "ItemStackSize: " + menuItemStack.size)
        return false
    }

    private fun showBottomNavigation(){
        bottom_navigation.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation(){
        bottom_navigation.visibility = View.GONE
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(destination.id){
            R.id.marketplaceFragment-> showBottomNavigation()
            R.id.loginFragment-> hideBottomNavigation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(menuItemStack.size > 1){
            menuItemStack.pop()
            menuItemStack.lastElement().isChecked = true
        }else{
            bottom_navigation.menu.setGroupCheckable(R.id.home_group, false, true)
        }
    }
}
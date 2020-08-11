package com.lendsumapp.lendsum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lendsumapp.lendsum.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener{

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottom_navigation.selectedItemId = R.id.marketplace
        hideBottomNavigation()
        setupBottomNavigation()

    }

    private fun setupBottomNavigation() {

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottom_navigation.setOnNavigationItemSelectedListener(this)

        navController.addOnDestinationChangedListener(this)

    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> {
                item.isChecked = true
                navController.navigate(R.id.profileFragment)
            }
            R.id.messages -> {
                navController.navigate(R.id.messagesFragment)
                item.isChecked = true
            }
            R.id.marketplace -> {
                navController.navigate(R.id.marketplaceFragment)
                item.isChecked = true
            }
            R.id.bundles -> {
                navController.navigate(R.id.bundlesFragment)
                item.isChecked = true
            }
            R.id.services -> {
                navController.navigate(R.id.servicesFragment)
                item.isChecked = true
            }
        }
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
        if (destination.id == R.id.marketplaceFragment){
            showBottomNavigation()
        }
    }

}
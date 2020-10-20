package com.lendsumapp.lendsum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.ActivityHomeBinding
import com.lendsumapp.lendsum.databinding.FragmentLoginBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_marketplace.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener{

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBottomNavigation()

    }

    private fun setupBottomNavigation() {
        AndroidUtils.hideView(binding.bottomNavigation)
        binding.bottomNavigation.menu.setGroupCheckable(R.id.home_group, false, true)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener(this)

    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isCheckable = true
        when(item.itemId){
            R.id.profile -> {
                item.isChecked = true
                navController.navigate(R.id.profileFragment)
            }
            R.id.messages -> {
                item.isChecked = true
                navController.navigate(R.id.messagesFragment)
            }
            R.id.marketplace -> {
                item.isChecked = true
                navController.navigate(R.id.marketplaceFragment)
            }
            R.id.bundles -> {
                item.isChecked = true
                navController.navigate(R.id.bundlesFragment)

            }
            R.id.services -> {
                item.isChecked = true
                navController.navigate(R.id.servicesFragment)

            }
        }

        return false
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        AndroidUtils.hideKeyboard(this)

        when(destination.id){
            R.id.profileFragment -> {
                binding.bottomNavigation.menu[0].isChecked = true
                AndroidUtils.showView(binding.bottomNavigation)
            }
            R.id.messagesFragment -> {
                binding.bottomNavigation.menu[1].isChecked = true
                AndroidUtils.showView(binding.bottomNavigation)
            }
            R.id.marketplaceFragment-> {
                binding.bottomNavigation.menu[2].isChecked = true
                AndroidUtils.showView(binding.bottomNavigation)
            }
            R.id.bundlesFragment ->{
                binding.bottomNavigation.menu[3].isChecked = true
                AndroidUtils.showView(binding.bottomNavigation)
            }
            R.id.servicesFragment ->{
                binding.bottomNavigation.menu[4].isChecked = true
                AndroidUtils.showView(binding.bottomNavigation)
            }
            R.id.loginFragment-> AndroidUtils.shrinkView(binding.bottomNavigation)
            R.id.editProfileFragment-> AndroidUtils.shrinkView(binding.bottomNavigation)
            R.id.chatRoomFragment -> AndroidUtils.shrinkView(binding.bottomNavigation)
        }
    }
}
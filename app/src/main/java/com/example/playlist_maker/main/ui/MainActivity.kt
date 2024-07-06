package com.example.playlist_maker.main.ui


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityMainBinding

import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        showNavBar()
    }

    fun showNavBar() {
        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.VISIBLE
        val view_p = findViewById<View>(R.id.view_p)
        view_p.visibility = View.VISIBLE
    }
    fun hideNavBar() {
        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE
        val view_p = findViewById<View>(R.id.view_p)
        view_p.visibility = View.GONE
    }
}

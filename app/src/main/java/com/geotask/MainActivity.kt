
package com.geotask

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Правильное получение NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // BottomNavigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.xrNavBar)

        // Связываем с Navigation
        bottomNav.setupWithNavController(navController)

        // Экраны без нижней панели
        val hiddenScreens = setOf(
            R.id.taskCreateFragment,
            R.id.taskDetailFragment,
            R.id.mapFragment,
            R.id.locationCreateFragment,
            R.id.locationDetailFragment
        )

        // Управление видимостью панели
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility =
                if (destination.id in hiddenScreens) View.GONE
                else View.VISIBLE
        }
    }
}


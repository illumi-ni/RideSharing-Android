package com.ujjallamichhane.ridesharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class DriverButtomNavActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_buttom_nav)
        val navView: BottomNavigationView = findViewById(R.id.nav_view_driver)

        val navController = findNavController(R.id.driverHostFragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_rides, R.id.navigation_settings
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
package com.ujjallamichhane.ridesharing

import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class CustomerBottomNav : AppCompatActivity() {

    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutomer_bottom_nav)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_rides, R.id.navigation_settings
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //check for permission
        if (!hasPermission()) {
            requestPermission()
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            permissions,
            1)
    }

    private fun hasPermission(): Boolean{
        var hasPermission = true
        for(permission in permissions){
            if(ActivityCompat.checkSelfPermission(
                    this,
                    permission
                )!= PackageManager.PERMISSION_GRANTED
            ){
                hasPermission = false
            }
        }
        return hasPermission
    }


}
package com.ujjallamichhane.ridesharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import com.ujjallamichhane.ridesharing.repository.DriverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", null)
        val driverEmail = sharedPref.getString("driverEmail", null)
        val driverPassword = sharedPref.getString("driverPassword", null)

        checkEmail(userEmail, driverEmail, driverPassword)
    }

    private fun checkEmail(userEmail: String?, driverEmail: String?, driverPassword: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (userEmail == null ) {

                    if(driverEmail == null && driverPassword == null){
                        withContext(Dispatchers.Main) {
                            startActivity(
                                Intent(
                                    this@SplashscreenActivity,
                                    SignInActivity::class.java
                                )
                            )
                            finish()
                        }
                    }else{
                        val driverRepository = DriverRepository()
                        val response =
                            driverRepository.checkDriver(driverEmail.toString(),
                                driverPassword.toString()
                            )

                        if (response.success == true) {
                            ServiceBuilder.token = "Bearer ${response.token}"

                            val driver: Driver =response.driverData!!
                            ServiceBuilder.driver = driver

                            startActivity(
                                Intent(
                                    this@SplashscreenActivity,
                                    DriverButtomNavActivity::class.java
                                )
                            )
                            finish()
                        }
                    }


                } else {
                    val customerRepository = CustomerRepository()
                    val response =
                        customerRepository.checkEmail(userEmail.toString())

                    if (response.success == true) {
                        ServiceBuilder.token = "Bearer ${response.token}"

                        val customer: Customer = response.customerData!!
                        ServiceBuilder.customer = customer

                        startActivity(
                            Intent(
                                this@SplashscreenActivity,
                                CustomerBottomNav::class.java
                            )
                        )
                        finish()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SplashscreenActivity, ex.toString(), Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this@SplashscreenActivity, SignInActivity::class.java))
                    finish()
                }
            }
        }
    }
}
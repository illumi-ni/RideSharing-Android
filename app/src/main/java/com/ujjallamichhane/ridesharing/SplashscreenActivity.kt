package com.ujjallamichhane.ridesharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashscreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val email = sharedPref.getString("email", null)

        checkEmail(email)
    }

    private fun checkEmail(email: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (email == null) {
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@SplashscreenActivity, SignInActivity::class.java))
                        finish()
                    }
                } else {
                    val customerRepository = CustomerRepository()
                    val response =
                        customerRepository.checkEmail(email.toString())

                    if (response.success == true) {
                        ServiceBuilder.token = "Bearer ${response.token}"

                        val customer: Customer =response.customerData!!
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
                    Toast.makeText(this@SplashscreenActivity, ex.toString(), Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@SplashscreenActivity, SignInActivity::class.java))
                    finish()
                }
            }
        }
    }
}
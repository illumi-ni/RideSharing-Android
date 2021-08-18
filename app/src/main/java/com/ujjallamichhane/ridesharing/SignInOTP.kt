package com.ujjallamichhane.ridesharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignInOTP : AppCompatActivity() {
    private lateinit var txtEmail: TextView
    private lateinit var txtResend: TextView
    private lateinit var etOTP: EditText
    private lateinit var btnVerify: Button
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_o_t_p)

        txtEmail = findViewById(R.id.txtEmail)
        etOTP = findViewById(R.id.etOTP)
        btnVerify = findViewById(R.id.btnVerify)
        txtResend = findViewById(R.id.txtResend)

        val text = "Did not receive code? Resend code".toSpannable()
        text[text.length-12 until text.length+1] = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val intent = Intent(this@SignInOTP, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
        txtResend.movementMethod = LinkMovementMethod()
        txtResend.text = text
        btnVerify.setOnClickListener {
            val email = txtEmail.text.toString()
            val otp = etOTP.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val customerRepository = CustomerRepository()
                    val response = customerRepository.verifyOtp(otp)
                    if (response.success == true){
                        userEmail = response.customerData?.email.toString()
                        ServiceBuilder.customer = response.customerData
                        withContext(Dispatchers.Main){
                            Toast.makeText(
                                    this@SignInOTP,
                                    "Verified",
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                        saveSharedPref()
                        val intent = Intent(this@SignInOTP, CustomerBottomNav::class.java)
                        startActivity(intent)
                    }
                }catch(ex: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                                this@SignInOTP,
                                ex.toString(),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun saveSharedPref(){
        val email = userEmail
        val sharedPref = getSharedPreferences("UserPreferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("userEmail", email)
        editor.apply()
    }
}
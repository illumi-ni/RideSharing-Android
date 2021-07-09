package com.ujjallamichhane.ridesharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class SignInOTP : AppCompatActivity() {
    private lateinit var txtEmail: TextView
    private lateinit var etOTP: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_o_t_p)

        txtEmail = findViewById(R.id.txtEmail)
        etOTP = findViewById(R.id.etOTP)


    }
}
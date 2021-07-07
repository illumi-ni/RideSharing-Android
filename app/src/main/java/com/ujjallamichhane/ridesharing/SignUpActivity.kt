package com.ujjallamichhane.ridesharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContact: EditText
    private lateinit var group: RadioGroup
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var rbOthers: RadioButton
    private lateinit var btnSignUp: Button
    private lateinit var tvLogIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //reference binding
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etContact = findViewById(R.id.etContact)
        group = findViewById(R.id.group)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
        rbOthers = findViewById(R.id.rbOthers)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogIn = findViewById(R.id.tvLogIn)
    }
}
package com.ujjallamichhane.ridesharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

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
    var gender = ""

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

        checkGender()
        btnSignUp.setOnClickListener {
            customerSignup()
        }
    }
    private fun checkGender(){
        group.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.rbMale->{
                    gender = rbMale.text.toString()
                }
                R.id.rbFemale->{
                    gender = rbFemale.text.toString()
                }
                R.id.rbOthers->{
                    gender = rbOthers.text.toString()
                }
            }
        }
    }
    private fun customerSignup(){
        val fullname = etFullName.text.toString()
        val email = etEmail.text.toString()
        val contact = etContact.text.toString()

        val customer = Customer(fullname=fullname, email=email, contact=contact, gender=gender)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerRepository = CustomerRepository()
                val response = customerRepository.registerCustomer(customer)
                if (response.success == true){
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            this@SignUpActivity,
                            "Customer registered",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }catch(ex: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        this@SignUpActivity,
                        ex.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}
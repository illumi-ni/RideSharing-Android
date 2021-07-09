package com.ujjallamichhane.ridesharing.fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.SignInOTP
import com.ujjallamichhane.ridesharing.SignUpActivity
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class CustomerSignInFragment : Fragment() {
    private lateinit var etEmailSignIn:EditText
    private lateinit var btnSignIn:Button
    private lateinit var tvSignUp:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_sign_in, container, false)

        etEmailSignIn = view.findViewById(R.id.etEmailSignIn)
        btnSignIn = view.findViewById(R.id.btnSignIn)
        tvSignUp = view.findViewById(R.id.tvSignUp)

        val text = "No account? Create one".toSpannable()
        text[text.length-10 until text.length+1] = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val intent = Intent(context, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
        tvSignUp.movementMethod = LinkMovementMethod()
        tvSignUp.text = text

        btnSignIn.setOnClickListener {
            val email = etEmailSignIn.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val customerRepository = CustomerRepository()
                    val response = customerRepository.loginCustomer(email)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                    context,
                                    "Verification code sent",
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                        val intent = Intent(context, SignInOTP::class.java)
                        context!!.startActivity(intent)
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                                context,
                                ex.toString(),
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }


            }

        }
        return view
    }

    companion object {

    }
}
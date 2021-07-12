package com.ujjallamichhane.ridesharing.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class DriverSignInFragment : Fragment() {
    private lateinit var etEmailDriver: EditText
    private lateinit var etPwdDriver: EditText
    private lateinit var btnDriverSignIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_driver_sign_in, container, false)
        etEmailDriver = view.findViewById(R.id.etEmailDriver)
        etPwdDriver = view.findViewById(R.id.etPwdDriver)
        btnDriverSignIn = view.findViewById(R.id.btnDriverSignIn)

        return view
    }


}
package com.ujjallamichhane.ridesharing.ui.driver

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import com.ujjallamichhane.ridesharing.repository.DriverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class DriverProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var imgBtnUpload: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etGender: EditText
    private lateinit var etLicense: EditText
    private lateinit var etCitizenship: EditText
    private lateinit var btnUpdate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_driver_profile, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        imgBtnUpload = view.findViewById(R.id.imgBtnUpload)
        etFullName = view.findViewById(R.id.etFullName)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etGender = view.findViewById(R.id.etGender)
        etLicense = view.findViewById(R.id.etLicence)
        etCitizenship = view.findViewById(R.id.etCitizenship)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        showUserDetails()

        imgBtnUpload.setOnClickListener{

        }

        btnUpdate.setOnClickListener{
            updateUserDetails()
        }

        return view
    }

    private fun showUserDetails(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val driverRepository = DriverRepository()
                val response = driverRepository.getDriverDetails()
                withContext(Dispatchers.Main) {
                    tvUsername.setText(ServiceBuilder.driver!!.username)
                    etFullName.setText(response.driverData!!.fullname)
                    etEmail.setText(response.driverData.email)
                    etPhone.setText(response.driverData.phone)
                    etGender.setText(response.driverData.gender)
                    etLicense.setText(response.driverData.licence)
                    etCitizenship.setText(response.driverData.citizenship)
                }

            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserDetails(){
        val fullname = etFullName.text.toString()
//        val username = .text.toString()
        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val gender = etGender.text.toString()
        val license = etLicense.text.toString()
        val citizenship = etCitizenship.text.toString()

        val driver = Driver( _id= ServiceBuilder.driver!!._id,fullname = fullname, email = email, phone = phone,
            gender = gender, licence = license, citizenship = citizenship)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val driverRepository = DriverRepository()
                val response = driverRepository.updateDriver(driver._id!!, driver)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    companion object {

    }
}
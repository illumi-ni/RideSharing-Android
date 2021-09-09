package com.ujjallamichhane.ridesharing.ui.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.repository.DriverRepository
import com.ujjallamichhane.ridesharing.ui.driver.driverHome.DriverMapsFragment
import com.ujjallamichhane.ridesharing.ui.driver.driverSettings.DriverSettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DriverVehiclesfragment : Fragment() {

    private lateinit var etVehiclesNo: EditText
    private lateinit var imgBack: ImageView
    private lateinit var etModel: EditText
    private lateinit var appBar: AppBarLayout
    private lateinit var btnUpdate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_driver_vehiclesfragment, container, false)
        etVehiclesNo = view.findViewById(R.id.etVehicleNo)
        etModel = view.findViewById(R.id.etModel)
        imgBack = view.findViewById(R.id.imgBack)
        appBar = view.findViewById(R.id.appBar)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        imgBack.setOnClickListener {
            val ft = requireView().context as AppCompatActivity
            ft.supportFragmentManager.beginTransaction()
                .replace(R.id.scheduleContainer, DriverSettingsFragment())
                .addToBackStack(null)
                .commit();
            appBar.visibility = View.GONE
            btnUpdate.visibility = View.GONE
        }

        showVehiclesDetails()

        btnUpdate.setOnClickListener {
            updateVehicleDetails()
        }


        return view
    }

    private fun showVehiclesDetails(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val driverRepository = DriverRepository()
                val response = driverRepository.getDriverDetails()
                withContext(Dispatchers.Main) {
                    etVehiclesNo.setText(ServiceBuilder.driver!!.vechileNo)
                    etModel.setText(response.driverData!!.model)
                }

            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateVehicleDetails(){

        val fullname = ServiceBuilder.driver!!.fullname
        val username = ServiceBuilder.driver!!.username
        val email = ServiceBuilder.driver!!.email
        val phone = ServiceBuilder.driver!!.phone
        val gender = ServiceBuilder.driver!!.gender
        val license = ServiceBuilder.driver!!.licence
        val citizenship = ServiceBuilder.driver!!.citizenship
        val vehiclesNo = etVehiclesNo.text.toString()
        val model = etModel.text.toString()

        val driver = Driver(_id= ServiceBuilder.driver!!._id,fullname = fullname,username = username, email = email, phone = phone,
            gender = gender, licence = license, citizenship = citizenship,vechileNo = vehiclesNo, model = model)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val driverRepository = DriverRepository()
                val response = driverRepository.updateDriver(driver._id!!, driver)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Vehicles updated successfully", Toast.LENGTH_SHORT).show()
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
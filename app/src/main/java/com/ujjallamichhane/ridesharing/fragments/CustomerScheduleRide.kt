package com.ujjallamichhane.ridesharing.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.repository.ScheduleRepository
import com.ujjallamichhane.ridesharing.ui.customer.rides.RidesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.http.HttpDate.format
import java.text.SimpleDateFormat
import java.util.*

class CustomerScheduleRide : Fragment() {
    private lateinit var etSchFullName: EditText
    private lateinit var etPickupLoc: EditText
    private lateinit var etDropLoc: EditText
    private lateinit var etPickupDate: EditText
    private lateinit var etPickupTime: EditText
    private lateinit var etSchDistance: EditText
    private lateinit var etSchPrice: EditText
    private lateinit var imgBack: ImageView
    private lateinit var btnScheduleRide: Button

    var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_schedule_ride, container, false)
            etSchFullName = view.findViewById(R.id.etSchFullName)
            etPickupLoc = view.findViewById(R.id.etPickupLoc)
            etDropLoc = view.findViewById(R.id.etDropLoc)
            etPickupDate = view.findViewById(R.id.etPickupDate)
            etPickupTime = view.findViewById(R.id.etPickupTime)
            etSchDistance = view.findViewById(R.id.etSchDistance)
            etSchPrice = view.findViewById(R.id.etSchPrice)
            imgBack = view.findViewById(R.id.imgBack)
            btnScheduleRide = view.findViewById(R.id.btnScheduleRide)

            etPickupDate.setOnClickListener{pickDate()}
            etPickupTime.setOnClickListener{pickTime()}

            imgBack.setOnClickListener {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction!!.replace(R.id.scheduleContainer, CustomerScheduledRideFragment())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }

            btnScheduleRide.setOnClickListener {
                insertRide()
            }

            etSchFullName.setText(ServiceBuilder.customer!!.fullname)
        return view
    }
    private fun insertRide(){
        val fullname = etSchFullName.text.toString()
        val pickupLocation= etPickupLoc.text.toString()
        val dropLocation = etDropLoc.text.toString()
        val pickupDate = etPickupDate.text.toString()
        val pickupTime = etPickupTime.text.toString()
        val rideDistance = etSchDistance.text.toString()
        val ridePrice = etSchPrice.text.toString()

        val rides=Rides(fullname = fullname, from = pickupLocation, to = dropLocation,
            date= pickupDate, time = pickupTime, distance = rideDistance, price = ridePrice)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleRepository = ScheduleRepository()
                val response = scheduleRepository.scheduleRide(rides)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Ride scheduled successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

    private fun pickDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, yearApptDate, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth/${monthOfYear + 1}/$yearApptDate"
                etPickupDate.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
        datePickerDialog.show()
    }
    private fun pickTime(){
        val c = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                val time = timeFormat.format(selectedTime.time)
                etPickupTime.setText(time)
            },
            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false
        )
        timePicker.show()
    }
    companion object {

    }
}
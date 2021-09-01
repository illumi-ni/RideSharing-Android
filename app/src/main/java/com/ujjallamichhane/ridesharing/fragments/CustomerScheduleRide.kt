package com.ujjallamichhane.ridesharing.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private lateinit var etSchContact: EditText
    private lateinit var etPickupLoc: Spinner
    private lateinit var etDropLoc: Spinner
    private lateinit var etPickupDate: EditText
    private lateinit var etPickupTime: EditText
    private lateinit var etSchDistance: EditText
    private lateinit var etSchPrice: EditText
    private lateinit var imgBack: ImageView
    private lateinit var btnScheduleRide: Button

    var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    private val location = arrayOf("Pashupatinath","Boudha","Swayambhunath","Thamel","Kapan","Patan","Lokanthali","Jamal",
        "Kupondole","Samakhushi","Tokha","Koteshwor","Jadibuti","New baneshwor","Mid-Baneshwor","Satdobato", "Maitighar",
        "Tripureshwor","Sundhara","Maitidevi","Sinamangal","Gaushala","Chabahil","Tinkune", "Kaushaltar","Gatthaghar",
        "Thimi","Balkumari","Gwarko","Ekantakuna","Suryabinayak","Dhobighat","Jawalakhel","Lagankhel","Pulchowk")
//    private val destination = arrayOf("Manager", "Accountant", "Clerk")

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
            etSchContact = view.findViewById(R.id.etSchContact)
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
                insertBooking()
            }

            etSchFullName.setText(ServiceBuilder.customer!!.fullname)
            etSchContact.setText(ServiceBuilder.customer!!.contact)

            locations()

        return view
    }
    private fun insertBooking(){
        val fullname = etSchFullName.text.toString()
        val contact = etSchContact.text.toString()
        val pickupLocation= location[etPickupLoc.selectedItemPosition]
        val dropLocation = location[etDropLoc.selectedItemPosition]
        val pickupDate = etPickupDate.text.toString()
        val pickupTime = etPickupTime.text.toString()
        val rideDistance = etSchDistance.text.toString()
        val ridePrice = etSchPrice.text.toString()

        val rides=Rides(fullname = fullname, contact = contact, from = pickupLocation, to = dropLocation,
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

    private fun locations(){
        etPickupLoc.adapter = ArrayAdapter(requireActivity().applicationContext,
            android.R.layout.simple_list_item_1, location)
        etPickupLoc.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    Toast.makeText(context, "Selected Post : $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }

        etDropLoc.adapter = ArrayAdapter(requireActivity().applicationContext,
            android.R.layout.simple_list_item_1, location)
        etDropLoc.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    Toast.makeText(context, "Selected Post : $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }
    }
    companion object {

    }
}
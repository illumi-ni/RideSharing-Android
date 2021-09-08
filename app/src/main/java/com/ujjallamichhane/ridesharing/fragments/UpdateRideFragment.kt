package com.ujjallamichhane.ridesharing.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.repository.ScheduleRepository
import com.ujjallamichhane.ridesharing.ui.customer.rides.RidesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UpdateRideFragment : Fragment() {
    private lateinit var appBar: AppBarLayout
    private lateinit var imgBack: ImageView
    private lateinit var etPickupLoc: Spinner
    private lateinit var etDropLoc: Spinner
    private lateinit var etPickupDate: EditText
    private lateinit var etPickupTime: EditText
    private lateinit var etSchDistance: EditText
    private lateinit var etSchPrice: EditText
    private lateinit var btnUpdate: Button

    var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    private val location = arrayOf("Pashupatinath","Boudha","Swayambhunath","Thamel","Kapan","Patan","Lokanthali","Jamal",
        "Kupondole","Samakhushi","Tokha","Koteshwor","Jadibuti","New baneshwor","Mid-Baneshwor","Satdobato", "Maitighar",
        "Tripureshwor","Sundhara","Maitidevi","Sinamangal","Gaushala","Chabahil","Tinkune", "Kaushaltar","Gatthaghar",
        "Thimi","Balkumari","Gwarko","Ekantakuna","Suryabinayak","Dhobighat","Jawalakhel","Lagankhel","Pulchowk")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_ride, container, false)
        appBar = view.findViewById(R.id.appBar)
        imgBack = view.findViewById(R.id.imgBack)
        etPickupLoc = view.findViewById(R.id.etPickupLoc)
        etDropLoc = view.findViewById(R.id.etDestination)
        etPickupDate = view.findViewById(R.id.etPickupDate)
        etPickupTime = view.findViewById(R.id.etPickupTime)
        etSchDistance = view.findViewById(R.id.etDistance)
        etSchPrice = view.findViewById(R.id.etPrice)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        val data: Rides = requireArguments().getSerializable("rides") as Rides

        etPickupDate.setText(data.date)
        etPickupTime.setText(data.time)
        etSchDistance.setText(data.distance)
        etSchPrice.setText(data.price)

        etPickupLoc.setSelection(location.indexOf(data.from))
        etPickupLoc.setSelection(location.indexOf(data.to))

        etPickupDate.setOnClickListener{pickDate()}
        etPickupTime.setOnClickListener{pickTime()}

        imgBack.setOnClickListener {
            onBack()
        }

        btnUpdate.setOnClickListener {
            updateBooking(data._id!!)
        }
        locations()

        return view
    }

    fun onBack() {
        val ft = requireView().context as AppCompatActivity
        ft.supportFragmentManager.beginTransaction()
            .replace(R.id.updateScheduleContainer, RidesFragment())
            .addToBackStack(null)
            .commit();
        btnUpdate.visibility = View.GONE
        appBar.visibility = View.GONE
    }

    private fun updateBooking(rID: String){
        val contact = ServiceBuilder.customer!!.contact
        val email = ServiceBuilder.customer!!.email
        val pickupLocation= location[etPickupLoc.selectedItemPosition]
        val dropLocation = location[etDropLoc.selectedItemPosition]
        val pickupDate = etPickupDate.text.toString()
        val pickupTime = etPickupTime.text.toString()
        val rideDistance = etSchDistance.text.toString()
        val ridePrice = etSchPrice.text.toString()

        val rides= Rides(
            id = rID, contact = contact, email = email, from = pickupLocation, to = dropLocation,
            date = pickupDate, time = pickupTime, distance = rideDistance, price = ridePrice
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleRepository = ScheduleRepository()
                val response = scheduleRepository.updateBooking(rides)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ride updated successfully", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
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
//                    Toast.makeText(context, "Selected Post : $selectedItem", Toast.LENGTH_SHORT).show()
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
//                    Toast.makeText(context, "Selected Post : $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object
}
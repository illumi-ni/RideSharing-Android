package com.ujjallamichhane.ridesharing.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.adapter.RideHistoryAdapter
import com.ujjallamichhane.ridesharing.adapter.ScheduleAdapter
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.repository.RideRepository
import com.ujjallamichhane.ridesharing.repository.ScheduleRepository
import com.ujjallamichhane.ridesharing.ui.customer.customerFragment.ProfileFragment
import com.ujjallamichhane.ridesharing.ui.driver.DriverProfileFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CustomerScheduledRideFragment : Fragment() {
    private lateinit var fabSchedule: FloatingActionButton
    private lateinit var rvViewScheduled: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_scheduled_ride, container, false)
        rvViewScheduled = view.findViewById(R.id.rvViewScheduled)
        fabSchedule = view.findViewById(R.id.fabSchedule)

        fabSchedule.setOnClickListener {
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction!!.replace(R.id.schedule_content, CustomerScheduleRide())
//            transaction.addToBackStack(null)
//            transaction.commit()
//            fabSchedule.visibility = View.GONE

            val ft = requireView().context as AppCompatActivity
                ft.supportFragmentManager.beginTransaction()
                .replace(R.id.history, CustomerScheduleRide())
                .addToBackStack(null)
                .commit();
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleRepository = ScheduleRepository()
                val response = scheduleRepository.getAllScheduleRides(ServiceBuilder.token!!)

                withContext(Main) {
                    rvViewScheduled.layoutManager = LinearLayoutManager(requireContext())
                    val adapter = ScheduleAdapter(response.data!!, requireContext())
                    rvViewScheduled.adapter = adapter
                }

            } catch (ex: IOException) {
                withContext(Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    companion object {

    }
}
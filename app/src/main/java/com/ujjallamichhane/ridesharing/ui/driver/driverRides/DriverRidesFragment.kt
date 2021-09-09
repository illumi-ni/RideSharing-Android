package com.ujjallamichhane.ridesharing.ui.driver.driverRides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.adapter.DriverRideHistoryAdapter
import com.ujjallamichhane.ridesharing.adapter.RideHistoryAdapter
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.repository.RideRepository
import com.ujjallamichhane.ridesharing.ui.customer.rides.RidesFragment
import com.ujjallamichhane.ridesharing.ui.driver.driverHome.DriverMapsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DriverRidesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imgBack: ImageView
    private lateinit var appBar: AppBarLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_driver_rides, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        imgBack = view.findViewById(R.id.imgBack)
        appBar = view.findViewById(R.id.appBar)

        imgBack.setOnClickListener {
            val ft = requireView().context as AppCompatActivity
            ft.supportFragmentManager.beginTransaction()
                .replace(R.id.scheduleContainer, DriverMapsFragment())
                .addToBackStack(null)
                .commit();
            appBar.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val rideRepository = RideRepository()
                val response = rideRepository.getDriverBookings(ServiceBuilder.token!!)

                withContext(Dispatchers.Main) {
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    val adapter = DriverRideHistoryAdapter(response.data!!, requireContext())
                    recyclerView.adapter = adapter
                }

            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

      return view
    }
}
package com.ujjallamichhane.ridesharing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.adapter.RideHistoryAdapter
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.repository.RideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CustomerHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val rideRepository = RideRepository()
                val response = rideRepository.getAllBookings(ServiceBuilder.token!!)

                withContext(Main) {
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    val adapter = RideHistoryAdapter(response.data!!, requireContext())
                    recyclerView.adapter = adapter
                }

            } catch (ex: IOException) {
                withContext(Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    //    companion object {
//    }
}
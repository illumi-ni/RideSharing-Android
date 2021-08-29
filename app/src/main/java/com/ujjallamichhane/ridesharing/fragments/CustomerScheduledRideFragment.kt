package com.ujjallamichhane.ridesharing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.ui.customer.customerFragment.ProfileFragment

class CustomerScheduledRideFragment : Fragment() {
    private lateinit var fabSchedule: FloatingActionButton
    private lateinit var rvViewScheduled: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_scheduled_ride, container, false)
            rvViewScheduled = view.findViewById(R.id.rvViewScheduled)
            fabSchedule = view.findViewById(R.id.fabSchedule)
            fabSchedule.visibility = View.VISIBLE

            fabSchedule.setOnClickListener{
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction!!.replace(R.id.schedule_content, CustomerScheduleRide())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }

        return view
    }

    companion object {

    }
}
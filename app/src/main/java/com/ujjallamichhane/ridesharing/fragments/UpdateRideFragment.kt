package com.ujjallamichhane.ridesharing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.ujjallamichhane.ridesharing.R

class UpdateRideFragment : Fragment() {
    private lateinit var etPickupDate: EditText
    private lateinit var etPickupTime: EditText
    private lateinit var etPickup: EditText
    private lateinit var etDrop: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_ride, container, false)
        etPickupDate = view.findViewById(R.id.etPickupDate)
        etPickupTime = view.findViewById(R.id.etPickupTime)
        etPickup = view.findViewById(R.id.etPickup)
        etDrop = view.findViewById(R.id.etDrop)

        return view
    }

    companion object {

    }
}
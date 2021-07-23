package com.ujjallamichhane.ridesharing.ui.driver.driverRides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ujjallamichhane.ridesharing.R

class DriverRidesFragment : Fragment() {

  private lateinit var driverRidesViewModel: DriverRidesViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    driverRidesViewModel =
            ViewModelProvider(this).get(DriverRidesViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_driver_rides, container, false)
    val textView: TextView = root.findViewById(R.id.text_dashboard)
    driverRidesViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })
    return root
  }
}
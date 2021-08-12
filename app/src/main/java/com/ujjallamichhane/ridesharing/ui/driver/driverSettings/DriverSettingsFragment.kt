package com.ujjallamichhane.ridesharing.ui.driver.driverSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ujjallamichhane.ridesharing.R

class DriverSettingsFragment : Fragment() {

  private lateinit var driverSettingsViewModel: DriverSettingsViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    driverSettingsViewModel =
            ViewModelProvider(this).get(DriverSettingsViewModel::class.java)
    val view = inflater.inflate(R.layout.fragment_driver_settings, container, false)
//    val textView: TextView = root.findViewById(R.id.text_notifications)
//    driverSettingsViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })

    return view
  }
}
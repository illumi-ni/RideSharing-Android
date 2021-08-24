package com.ujjallamichhane.ridesharing.ui.driver.driverSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.widget.SimpleAdapter
import com.ujjallamichhane.ridesharing.R
import android.icu.number.Precision.currency





class DriverSettingsFragment : Fragment() {

  // Array of strings for ListView Title
  private var listviewTitle = arrayOf(
    "Personal Information", "Vehicles Details", "Earnings", "Notifications", "Rate & Reviews", "Logout"
  )

  private var listviewImage = intArrayOf(
    R.drawable.ic_info, R.drawable.ic_rides, R.drawable.ic_payment,
    R.drawable.ic_bell, R.drawable.ic_star, R.drawable.ic_logout
  )

  private lateinit var listView : ListView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_driver_settings, container, false)

    // Each row in the list stores icon and Title
    val aList: MutableList<HashMap<String, String>> = ArrayList()

    for (i in 0..5) {
      val hm = HashMap<String, String>()
      hm["listview_image"] = listviewImage[i].toString()
      hm["listview_title"] = listviewTitle[i]
      aList.add(hm)
    }

    val from = arrayOf("listview_image", "listview_title")
    val to = intArrayOf(
      R.id.imgIcon,
      R.id.tvText,
    )

    val adapter = SimpleAdapter(context, aList, R.layout.driver_setting_layout, from, to)
    listView = view.findViewById(R.id.listView)
    listView.adapter = adapter

    return view
  }
}
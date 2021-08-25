package com.ujjallamichhane.ridesharing.ui.driver.driverSettings

import android.R.attr
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
import android.widget.LinearLayout

import android.widget.AdapterView

import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
//import com.ujjallamichhane.ridesharing.ui.driver.DriverProfileFragment
import android.R.attr.country

import android.R.attr.name
import android.content.Context
import android.content.DialogInterface

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.ujjallamichhane.ridesharing.SignInActivity
//import com.ujjallamichhane.ridesharing.ui.driver.DriverEarningsFragment
import com.ujjallamichhane.ridesharing.ui.driver.DriverProfileFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DriverSettingsFragment : Fragment() {

    // Array of strings for ListView Title
    private var listviewTitle = arrayOf(
        "Personal Information",
        "Vehicles Details",
        "Earnings",
        "Notifications",
        "Rate & Reviews",
        "Logout"
    )

    private var listviewImage = intArrayOf(
        R.drawable.ic_info, R.drawable.ic_rides, R.drawable.ic_payment,
        R.drawable.ic_bell, R.drawable.ic_star, R.drawable.ic_logout
    )

    private lateinit var listView: ListView

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
        val to = intArrayOf(R.id.imgIcon, R.id.tvText,)
        val adapter = SimpleAdapter(context, aList, R.layout.driver_setting_layout, from, to)
        listView = view.findViewById(R.id.listView)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val ft = requireView().context as AppCompatActivity

            when (position) {
                0 -> ft.supportFragmentManager.beginTransaction()
                    .replace(R.id.driver_setting, DriverProfileFragment()).addToBackStack(null)
                    .commit();

                1 -> ft.supportFragmentManager.beginTransaction()
                    .replace(R.id.driver_setting, DriverProfileFragment()).addToBackStack(null)
                    .commit();

//                2 -> ft.supportFragmentManager.beginTransaction()
//                    .replace(R.id.driverHostFragment, DriverEarningsFragment()).addToBackStack(null)
//                    .commit();

                3 -> ft.supportFragmentManager.beginTransaction()
                    .replace(R.id.driverHostFragment, DriverProfileFragment()).addToBackStack(null)
                    .commit();

                4 -> ft.supportFragmentManager.beginTransaction()
                    .replace(R.id.driverHostFragment, DriverProfileFragment()).addToBackStack(null)
                    .commit();

                5 -> logout()

            }
        }

        return view
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Log out?")
        builder.setIcon(R.drawable.ic_logout)
        builder.setPositiveButton("Yes") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val preferences = requireActivity().getSharedPreferences(
                        "UserPreferences",
                        Context.MODE_PRIVATE
                    )
                    val editor = preferences.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(context, SignInActivity::class.java))

                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        builder.setNegativeButton("No") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}



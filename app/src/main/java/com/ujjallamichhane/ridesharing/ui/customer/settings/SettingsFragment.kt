package com.ujjallamichhane.ridesharing.ui.customer.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ujjallamichhane.ridesharing.R

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var tvEditProfile: TextView
    private lateinit var tvLogout: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        settingsViewModel =
//            ViewModelProvider(this).get(SettingsViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_customer_settings, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        tvEditProfile = view.findViewById(R.id.tvEditProfile)
        tvLogout = view.findViewById(R.id.tvLogout)

        tvEditProfile.setOnClickListener {

        }



        return view
    }
}
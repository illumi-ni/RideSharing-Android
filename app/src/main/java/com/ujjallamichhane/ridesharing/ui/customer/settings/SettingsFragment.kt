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
import com.ujjallamichhane.ridesharing.ui.customer.customerFragment.ProfileFragment

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var tvEditProfile: TextView
    private lateinit var tvNotification: TextView
    private lateinit var tvPayment: TextView
    private lateinit var tvShareLocation: TextView

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
        tvNotification = view.findViewById(R.id.tvNotification)
        tvPayment = view.findViewById(R.id.tvPayment)
        tvShareLocation = view.findViewById(R.id.tvShareLocation)
        tvLogout = view.findViewById(R.id.tvLogout)

        tvEditProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction!!.replace(R.id.settings_container, ProfileFragment())
            transaction.disallowAddToBackStack()
            transaction.commit()
            }


        return view
    }
}
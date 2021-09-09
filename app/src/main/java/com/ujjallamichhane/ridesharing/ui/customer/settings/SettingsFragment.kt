package com.ujjallamichhane.ridesharing.ui.customer.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.SignInActivity
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import com.ujjallamichhane.ridesharing.repository.DriverRepository
import com.ujjallamichhane.ridesharing.ui.customer.customerFragment.ProfileFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var tvEditProfile: TextView
    private lateinit var customerName: TextView
    private lateinit var customerPhone: TextView
    private lateinit var imgCustProfile: CircleImageView
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
        customerName = view.findViewById(R.id.customerName)
        customerPhone = view.findViewById(R.id.customerPhone)
        tvShareLocation = view.findViewById(R.id.tvShareLocation)
        imgCustProfile = view.findViewById(R.id.imgCustProfile)
        tvLogout = view.findViewById(R.id.tvLogout)

        tvEditProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction!!.replace(R.id.settings_container, ProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }


        tvLogout.setOnClickListener {
            logout()
        }

        customerName.setText(ServiceBuilder.customer!!.fullname)
        customerPhone.setText(ServiceBuilder.customer!!.contact)
        loadImage()

        return view
    }

    private fun loadImage() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerRepository = CustomerRepository()
                val response = customerRepository.getCustomerDetails()
                withContext(Dispatchers.Main) {
                    if (ServiceBuilder.customer!!.photo.equals("")) {
                        Glide.with(requireContext())
                            .load(R.drawable.noimg)
                            .into(imgCustProfile)
                    } else {
//
                        val imagePath = ServiceBuilder.BASE_URL + response.customerData!!.photo
                        Glide.with(requireContext())
                            .load(imagePath)
                            .fitCenter()
                            .into(imgCustProfile)
                    }
                }
            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun logout() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Log out?")
        builder.setIcon(R.drawable.ic_logout)
        builder.setPositiveButton("Yes") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val preferences = requireActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(context,SignInActivity::class.java)
                    )
                }
                catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, ex.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNegativeButton("No") { _, _ ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
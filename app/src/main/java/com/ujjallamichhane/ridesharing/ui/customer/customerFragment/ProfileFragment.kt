package com.ujjallamichhane.ridesharing.ui.customer.customerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ProfileFragment : Fragment() {
    private lateinit var imgProfileCustomer: CircleImageView
    private lateinit var imgBtnUpload: ImageView
    private lateinit var etCustomerFullname: EditText
    private lateinit var etCustomerEmail: EditText
    private lateinit var etCustomerPhone: EditText
    private lateinit var etCustomerGender: EditText
    private lateinit var btnUpdateCProfile: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
            imgProfileCustomer = view.findViewById(R.id.imgProfileCustomer)
            imgBtnUpload= view.findViewById(R.id.imgBtnUpload)
            etCustomerFullname= view.findViewById(R.id.etCustomerFullname)
            etCustomerEmail= view.findViewById(R.id.etCustomerEmail)
            etCustomerPhone= view.findViewById(R.id.etCustomerPhone)
            etCustomerGender= view.findViewById(R.id.etCustomerGender)
            btnUpdateCProfile= view.findViewById(R.id.btnUpdateCProfile)

            customerDetails()
        return view
    }

    private fun customerDetails(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerRepository = CustomerRepository()
                val response = customerRepository.getCustomerDetails()
                withContext(Dispatchers.Main) {
                    etCustomerFullname.setText(response.data!!.fullname)
                    etCustomerEmail.setText(response.data.email)
                    etCustomerPhone.setText(response.data.contact)
                    etCustomerGender.setText(response.data.gender)

//                    val imagePath = ServiceBuilder.BASE_URL + response.data.photo
//
//                    Glide.with(requireContext())
//                        .load(imagePath)
//                        .fitCenter()
//                        .into(imgProfile)
                }


            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
    }
}
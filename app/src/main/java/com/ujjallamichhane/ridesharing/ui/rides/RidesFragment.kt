package com.ujjallamichhane.ridesharing.ui.rides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ujjallamichhane.ridesharing.R

class RidesFragment : Fragment() {

    private lateinit var ridesViewModel: RidesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ridesViewModel =
            ViewModelProvider(this).get(RidesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_rides, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        ridesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
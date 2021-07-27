package com.ujjallamichhane.ridesharing.ui.customer.rides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.adapter.ViewPagerAdapter
import com.ujjallamichhane.ridesharing.fragments.CustomerSignInFragment
import com.ujjallamichhane.ridesharing.fragments.DriverSignInFragment

class RidesFragment : Fragment() {

    private lateinit var ridesViewModel: RidesViewModel
    private lateinit var lstFragments: ArrayList<Fragment>
    private lateinit var lstTitle: ArrayList<String>
    private lateinit var rdViewPager: ViewPager2
    private lateinit var rdTabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        ridesViewModel =
//            ViewModelProvider(this).get(RidesViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_customer_rides, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        ridesViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        rdViewPager = view.findViewById(R.id.rdViewPager)
        rdTabLayout = view.findViewById(R.id.rdTabLayout)
        populateList()
        val adapter = ViewPagerAdapter(lstFragments, childFragmentManager, lifecycle)
        rdViewPager.adapter = adapter
        TabLayoutMediator(rdTabLayout, rdViewPager){ tab, position ->
            tab.text = lstTitle[position]
        }.attach()

        return view
    }
    private fun populateList(){
        lstTitle = ArrayList<String>()
        lstTitle.add("History")
        lstTitle.add("Scheduled")
        lstFragments = ArrayList<Fragment>()
        lstFragments.add(CustomerSignInFragment())
        lstFragments.add(DriverSignInFragment())
    }
}
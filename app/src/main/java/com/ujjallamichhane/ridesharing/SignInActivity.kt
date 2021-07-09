package com.ujjallamichhane.ridesharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ujjallamichhane.ridesharing.adapter.ViewPagerAdapter
import com.ujjallamichhane.ridesharing.fragments.CustomerSignInFragment
import com.ujjallamichhane.ridesharing.fragments.DriverSignInFragment

class SignInActivity : AppCompatActivity() {
    private lateinit var lstFragments: ArrayList<Fragment>
    private lateinit var lstTitle: ArrayList<String>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        populateList()
        val adapter = ViewPagerAdapter(lstFragments, supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = lstTitle[position]
        }.attach()

    }
    private fun populateList(){
        lstTitle = ArrayList<String>()
        lstTitle.add("Customer")
        lstTitle.add("Driver")
        lstFragments = ArrayList<Fragment>()
        lstFragments.add(CustomerSignInFragment())
        lstFragments.add(DriverSignInFragment())
    }
}
package com.ujjallamichhane.ridesharing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.appbar.AppBarLayout
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.entity.Earning
import com.ujjallamichhane.ridesharing.ui.customer.rides.RidesFragment
import com.ujjallamichhane.ridesharing.ui.driver.driverSettings.DriverSettingsFragment

class DriverEarningsFragment : Fragment() {

    private lateinit var lineChart: LineChart
    private lateinit var tvEarnings: TextView
    private lateinit var tvTrip: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvLife: TextView
    private lateinit var appBar: AppBarLayout
    private lateinit var imgBack: ImageView

    private var earnings = ArrayList<Earning>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_driver_earnings, container, false)
            lineChart = view.findViewById(R.id.lineChart)
            tvEarnings = view.findViewById(R.id.tvEarnings)
            tvLife = view.findViewById(R.id.tvLife)
            tvTrip = view.findViewById(R.id.tvTrip)
            tvTotal = view.findViewById(R.id.tvTotal)
            appBar = view.findViewById(R.id.appBar)
            imgBack = view.findViewById(R.id.imgBack)

            imgBack.setOnClickListener {
                val ft = requireView().context as AppCompatActivity
                ft.supportFragmentManager.beginTransaction()
                    .replace(R.id.earningsContainer, DriverSettingsFragment())
                    .addToBackStack(null)
                    .commit();
                appBar.visibility = View.GONE
            }

            initLineChart()
            setDataToLineChart()
        return view
    }

    private fun initLineChart() {

//        hide grid lines
        lineChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        lineChart.axisRight.isEnabled = false

        //remove legend
        lineChart.legend.isEnabled = false


        //remove description label
        lineChart.description.isEnabled = false


        //add animation
        lineChart.animateX(1000, Easing.EaseInSine)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 90f

    }


    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < earnings.size) {
                earnings[index].trips
            } else {
                ""
            }
        }
    }

    private fun setDataToLineChart() {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

        earnings = getEarnings()

        //you can replace this data object with  your custom object
        for (i in earnings.indices) {
            val earning = earnings[i]
            entries.add(Entry(i.toFloat(), earning.income.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "")

        val data = LineData(lineDataSet)
        lineChart.data = data

        lineChart.invalidate()
    }

    // simulate api call
    // we are initialising it directly
    private fun getEarnings(): ArrayList<Earning> {
        earnings.add(Earning("Sunday", 56))
        earnings.add(Earning("Monday", 75))
        earnings.add(Earning("Tuesday", 45))
        earnings.add(Earning("Wednesday", 78))
        earnings.add(Earning("Thursday", 88))
        earnings.add(Earning("Friday", 56))
        earnings.add(Earning("Saturday", 40))

        return earnings
    }


    companion object {

    }
}
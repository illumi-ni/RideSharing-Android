package com.ujjallamichhane.ridesharing.ui.driver.driverHome

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ujjallamichhane.ridesharing.R
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.ujjallamichhane.ridesharing.DriverButtomNavActivity
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.entity.RideRequest
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import java.lang.Exception

class DriverMapsFragment : Fragment() {

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private lateinit var btnCurrentLocation: FloatingActionButton
    private lateinit var btnAcceptRequest: Button
    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    private var currentMarker: Marker? = null
    private var currentLocation: LatLng = LatLng(20.5, 78.9)
    private var currentDialog: Dialog? = null
    private lateinit var onlineSwitch: SwitchCompat
    private lateinit var tvPickUpDate: TextView
    private lateinit var tvPickUpLocation: TextView
    private lateinit var tvDestination: TextView
    private lateinit var tvDriversName: TextView
    private lateinit var tvFare: TextView
    private lateinit var tvDistance: TextView
    private var mSocket : Socket? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap = googleMap
        getLocation()

//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_driver_maps, container, false)
        val llBottomSheet = view.findViewById(R.id.bottom_sheet) as LinearLayout
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)
        onlineSwitch = view.findViewById(R.id.onlineSwitch)
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {}
            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        })

        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        btnCurrentLocation.setOnClickListener {
            getLocation()

        }

        onlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The following lines connects the Android app to the server.
                ServiceBuilder.setSocket()
                ServiceBuilder.establishConnection()
                mSocket = ServiceBuilder.getSocket()

                requestRideBottomSheet()
                Toast.makeText(context, "Checked", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "Not", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        mMap.clear()
                        val marker = mMap.addMarker(
                            MarkerOptions().position(currentLocation).icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            ).title("Current location")
                        )
                        marker.showInfoWindow()
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18F))
                        currentMarker = null
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    //check if location is enabled on the device
    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            pERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun requestRideBottomSheet() {
        if(ServiceBuilder.driver!=null){
            Log.d("Request Ride", ServiceBuilder.driver!!._id.toString())
            mSocket!!.on("driver_"+ServiceBuilder.driver!!._id.toString()) { args ->
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("Request ride", args.toString())
                        if (args[0] != null) {
                            val counter = args[0] as JSONObject
                            val gson = Gson()
                            val data = gson.fromJson(counter.toString(), RideRequest::class.java)

                            withContext(Dispatchers.Main) {
                                Log.d("Request Ride", "True")
                                currentDialog = BottomSheetDialog(requireContext())
                                val view = layoutInflater.inflate(R.layout.request_ride, null)
                                currentDialog!!.setContentView(view)
                                currentDialog!!.show()

                                tvPickUpLocation = view.findViewById(R.id.tvPickUpLocation)
                                tvDestination = view.findViewById(R.id.tvDestination)
                                tvDriversName = view.findViewById(R.id.tvDriversNme)
                                tvFare = view.findViewById(R.id.tvFare)
                                tvDistance = view.findViewById(R.id.tvDistance)
                                tvPickUpDate = view.findViewById(R.id.tvPickUpDate)
                                btnAcceptRequest = view.findViewById(R.id.btnAcceptRequest)

                                Log.d("Request Ride", data.toString())
                                tvPickUpDate.text = data.date
                                tvPickUpLocation.text = data.from
                                tvDestination.text = data.to
                                tvDriversName.text = data.fullname
                                tvFare.text = data.price
                                tvDistance.text = data.distance
                                Toast.makeText(context, "$data", Toast.LENGTH_LONG).show()

                                btnAcceptRequest.setOnClickListener{

                                        val gson: Gson = Gson()
                                        val ad = gson.toJson(
                                            Driver( _id = ServiceBuilder.driver!!._id,
                                                fullname = ServiceBuilder.driver!!.fullname,
                                                phone = ServiceBuilder.driver!!.phone,
                                                vechileNo = ServiceBuilder.driver!!.vechileNo,
                                                model = ServiceBuilder.driver!!.model
                                            )
                                        )

                                        mSocket!!.emit("accept", ad)

                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
package com.ujjallamichhane.ridesharing.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ujjallamichhane.ridesharing.R
import java.io.IOException
import java.util.*


class MapsFragment : Fragment() {

    //Declaring the needed Variables
    private lateinit var btnCurrentLocation: FloatingActionButton
    private lateinit var etHello: EditText
    private lateinit var etSetLocation: EditText
    private lateinit var tvDistance: TextView
    private lateinit var tvPrice: TextView
    private lateinit var btnConfirm: Button
    private lateinit var btnRequest: Button
    private lateinit var lollipop: ImageView

    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    var geo: Geocoder? = null

    var currentMarker: Marker? = null
    var currentDialog: BottomSheetDialog? = null
    var addresses: List<Address>? = null

    var currentLocation: LatLng = LatLng(20.5, 78.9)
    var cameraPos: LatLng = LatLng(0.0, 0.0)

    var distance: Double = 0.0


    @SuppressLint("SetTextI18n")
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)
        etHello = view.findViewById(R.id.etHello)
        lollipop = view.findViewById(R.id.lollipop)

        btnCurrentLocation.setOnClickListener {
            getLocation()
        }

        etHello.setOnClickListener {
            any()
            lollipop.isVisible = true
        }

        val ai: ApplicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }

        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        return view
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
                        mMap.addMarker(
                            MarkerOptions().position(currentLocation).icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            )
                        )
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

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    fun any () {
        mMap.setOnCameraMoveListener {
            val position = mMap.cameraPosition.target
            if (currentMarker == null) {
                currentMarker = mMap.addMarker(
                    MarkerOptions().position(position).visible(false)
//                        .icon(BitmapFromVector(requireContext(), R.drawable.ic_lollipop))
                )
            }
            else {
                currentMarker!!.position = position
                cameraPos = LatLng(position.latitude, position.longitude)
            }
        }

        mMap.setOnCameraIdleListener {
            geo = Geocoder(requireContext(), Locale.getDefault())

            try {
                addresses = geo!!.getFromLocation(cameraPos.latitude, cameraPos.longitude, 1)
                val address: String = addresses!![0].getAddressLine(0)
//                    etHello.setText(address)
                    showBottomSheetDialog(address)

            } catch (e: IOException) {
                Toast.makeText(requireContext(), "${e.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBottomSheetDialog(address: String){
        if(currentDialog == null) {
            currentDialog = BottomSheetDialog(requireContext())
            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            etSetLocation = view.findViewById(R.id.etSetLocation)
            btnConfirm = view.findViewById(R.id.btnConfirm)


            etSetLocation.setText(address)

            currentDialog!!.setContentView(view)
            currentDialog!!.show()
            btnConfirm.setOnClickListener {
                currentDialog!!.dismiss()

                sendRequestBottomSheet()
            }
        }
        else{
            currentDialog = null
            showBottomSheetDialog(address)
        }
    }

    private fun sendRequestBottomSheet(){
            currentDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.send_request, null)
            tvDistance = view.findViewById(R.id.tvDistance)
            tvPrice = view.findViewById(R.id.tvPrice)
            btnRequest = view.findViewById(R.id.btnRequest)

            currentDialog!!.setContentView(view)
            currentDialog!!.show()

    }

//    private fun calculateDistance(){
//
//        getLocation()
//
//        val locationA = Location("")
//        locationA.latitude = main_Latitude
//        locationA.longitude = main_Longitude
//        val locationB = Location("")
//        locationB.latitude = sub_Latitude
//        locationB.longitude = sub_Longitude
//        distance = (locationA.distanceTo(locationB) / 1000).toDouble()
//        kmeter.setText(distance.toString())
//        Toast.makeText(getApplicationContext(), "" + distance, Toast.LENGTH_LONG).show()
//        var distance: Double
//    }
}
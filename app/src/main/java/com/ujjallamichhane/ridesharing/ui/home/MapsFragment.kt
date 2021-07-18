package com.ujjallamichhane.ridesharing.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
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
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
    private lateinit var btnConfirm: Button

    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    var geo: Geocoder? = null

    var currentMarker: Marker? = null
    var addresses: List<Address>? = null

    var currentLocation: LatLng = LatLng(20.5, 78.9)
    var cameraPos: LatLng = LatLng(0.0, 0.0)


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

        btnCurrentLocation.setOnClickListener {
            getLocation()
        }

        etHello.setOnClickListener {
            any()
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
                        currentMarker = null;
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
        mMap.setOnCameraChangeListener { cameraPosition ->
            if (currentMarker == null) {
                currentMarker = mMap.addMarker(
                    MarkerOptions().position(cameraPosition.target).visible(false)
//                        .icon(BitmapFromVector(requireContext(), R.drawable.ic_lollipop))
                )
            }
            else {
                currentMarker!!.position = cameraPosition.target
                cameraPos = LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)


                geo = Geocoder(requireContext(), Locale.getDefault())

                // Address found using the Geocoder.
                try {
                    // Using getFromLocation() returns an array of Addresses for the area immediately
                    // surrounding the given latitude and longitude. The results are a best guess and are
                    // not guaranteed to be accurate.
                    addresses = geo!!.getFromLocation(cameraPos.latitude, cameraPos.longitude, 1)
                    val address: String = addresses!![0].getAddressLine(0)
                    etHello.setText(address)
                    showBottomSheetDialog(address)

                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "${e.toString()}", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun showBottomSheetDialog(address: String){
        val dialog = BottomSheetDialog(requireContext())
        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        etSetLocation = view.findViewById(R.id.etSetLocation)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        etSetLocation.setText(address)
        dialog.setContentView(view)
        dialog.show()
    }

//    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
//        // below line is use to generate a drawable.
//        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
//
//        // below line is use to set bounds to our vector drawable.
//        vectorDrawable!!.setBounds(
//            0,
//            0,
//            vectorDrawable.intrinsicWidth,
//            vectorDrawable.intrinsicHeight
//        )
//
//        // below line is use to create a bitmap for our
//        // drawable which we have added.
//        val bitmap = Bitmap.createBitmap(
//            vectorDrawable.intrinsicWidth,
//            vectorDrawable.intrinsicHeight,
//            Bitmap.Config.ARGB_8888
//        )
//
//        // below line is use to add bitmap in our canvas.
//        val canvas = Canvas(bitmap)
//
//        // below line is use to draw our
//        // vector drawable in canvas.
//        vectorDrawable.draw(canvas)
//
//        // after generating our bitmap we are returning our bitmap.
//        return BitmapDescriptorFactory.fromBitmap(bitmap)
//    }
}
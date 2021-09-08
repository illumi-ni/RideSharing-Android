package com.ujjallamichhane.ridesharing.ui.driver.driverHome

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.entity.RideRequest
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import android.R.attr.name
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ujjallamichhane.ridesharing.NotificationChannels
import com.ujjallamichhane.ridesharing.SignInActivity
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import com.ujjallamichhane.ridesharing.repository.RideRepository
import io.socket.emitter.Emitter


class DriverMapsFragment : Fragment() {

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private lateinit var btnCurrentLocation: FloatingActionButton
    private lateinit var btnAcceptRequest: Button
    private lateinit var btnNavigate: Button
    private lateinit var btnCancel: Button
    private lateinit var btnEnd: Button
    private lateinit var btnStart: Button
    private lateinit var fabCall: FloatingActionButton
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
    private lateinit var imgCustomer: CircleImageView
    private lateinit var tvCustomersName: TextView
    private lateinit var tvFare: TextView
    private lateinit var tvDistance: TextView
    private var mSocket: Socket? = null

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

                mSocket!!.on("cuCanceled" + ServiceBuilder.driver!!._id.toString(), showNotification)
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
        if (ServiceBuilder.driver != null) {
            Log.d("Request Ride", ServiceBuilder.driver!!._id.toString())
            mSocket!!.on("driver_" + ServiceBuilder.driver!!._id.toString()) { args ->
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
                                val view = layoutInflater.inflate(R.layout.request_ride_layout, null)
                                currentDialog!!.setContentView(view)
                                currentDialog!!.show()

                                tvPickUpLocation = view.findViewById(R.id.tvPickUpLocation)
                                tvDestination = view.findViewById(R.id.tvDestination)
                                tvCustomersName = view.findViewById(R.id.tvCustomersName)
                                imgCustomer = view.findViewById(R.id.imgCustomer)
                                tvFare = view.findViewById(R.id.tvFare)
                                tvDistance = view.findViewById(R.id.tvDistance)
                                tvPickUpDate = view.findViewById(R.id.tvPickUpDate)
                                btnAcceptRequest = view.findViewById(R.id.btnAcceptRequest)

//                                Log.d("Request Ride", data.toString())
                                tvPickUpDate.text = data.date
                                tvPickUpLocation.text = data.from
                                tvDestination.text = data.to
                                tvCustomersName.text = data.customer!!.fullname
                                tvFare.text = data.price
                                tvDistance.text = data.distance
                                Glide.with(requireContext())
                                    .load(ServiceBuilder.BASE_URL+data.customer.photo)
                                    .into(imgCustomer)
//                                Toast.makeText(context, "$data", Toast.LENGTH_LONG).show()

                                btnAcceptRequest.setOnClickListener {

                                    val gson: Gson = Gson()
                                    val ad = gson.toJson(ServiceBuilder.driver)
                                    mSocket!!.emit("accept", ad)
                                    currentDialog!!.dismiss()
                                    waitingPassenger(data)
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

    private fun waitingPassenger(data: RideRequest) {
        currentDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.waiting_passenger_layout, null)
        tvPickUpLocation = view.findViewById(R.id.tvPickUpLocation)
        tvCustomersName = view.findViewById(R.id.tvCustomersName)
        imgCustomer = view.findViewById(R.id.imgCustomer)
        btnNavigate = view.findViewById(R.id.btnNavigate)
        tvFare = view.findViewById(R.id.tvFare)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnStart = view.findViewById(R.id.btnStart)

        tvPickUpLocation.text = data.from
        tvCustomersName.text = data.customer!!.fullname
        tvFare.text = data.price
        Glide.with(requireContext())
            .load(ServiceBuilder.BASE_URL+data.customer.photo)
            .into(imgCustomer)

        currentDialog!!.setContentView(view)
        currentDialog!!.show()

        btnNavigate.setOnClickListener {
            loadNavigation(data)
        }

        btnCancel.setOnClickListener {
            val message = "Your ride request has been cancelled"
            mSocket!!.emit("driverCancel", message)
        }

        btnStart.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Arrived at pick up location?")
            builder.setPositiveButton("Yes") { _, _ ->
                currentDialog!!.dismiss()
                rideEnd(data)
            }

            builder.setNegativeButton("No") { _, _ ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()
        }
    }

    private var showNotification = Emitter.Listener{
        val counter = it[0]
        val notificationManager = NotificationManagerCompat.from(requireContext())

        val notificationChannels = NotificationChannels(requireContext())
        notificationChannels.createNotificationChannels()

        val notification = NotificationCompat.Builder(requireContext(), notificationChannels.CHANNEL_1)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle("Ride Sharing")
            .setContentText(counter.toString())
            .setColor(Color.GREEN)
            .build()

        currentDialog!!.dismiss()
        currentDialog = null;

        notificationManager.notify(1, notification)

    }

    private fun loadNavigation(data:RideRequest){
        val navigation: Uri =
            Uri.parse("google.navigation:q=" + data.from)
        val navigationIntent = Intent(Intent.ACTION_VIEW, navigation)
        navigationIntent.setPackage("com.google.android.apps.maps")
        startActivity(navigationIntent)
    }

    private fun rideEnd(data: RideRequest){
        currentDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.ride_complete_layout, null)
        tvDestination = view.findViewById(R.id.tvDestination)
        btnNavigate = view.findViewById(R.id.btnNavigate)
        btnEnd = view.findViewById(R.id.btnEnd)
        tvDestination.text = data.to
        currentDialog!!.setContentView(view)
        currentDialog!!.show()

        btnEnd.setOnClickListener {
            insertRide(data)
        }
    }

    private fun insertRide(ride: RideRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val rideRepository = RideRepository()
                val rideRequest = RideRequest(
                    from = ride.from, to = ride.to, date = ride.date, distance = ride.distance,
                    price = ride.price, customer = ride.customer, driver = ServiceBuilder.driver
                )
                val response = rideRepository.insertRide(rideRequest)

                if (response.success == true){
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            context,
                            "hello",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }catch(ex: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        context,
                        ex.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

package com.ujjallamichhane.ridesharing.ui.customer.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.directions.route.*
import com.directions.route.Route
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.maps.android.SphericalUtil
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.entity.RideRequest
import okhttp3.*
import okio.*
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.Objects.toString
import kotlin.collections.ArrayList

import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import android.R.attr.name
import com.bumptech.glide.Glide
import com.ujjallamichhane.ridesharing.entity.Customer
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DateFormat

import android.R.attr.name
import com.google.android.gms.maps.model.LatLng

import android.R.attr.name

import android.R.attr.name
import android.view.inputmethod.EditorInfo

import android.widget.TextView

import android.R.attr.name
import android.content.ContentValues.TAG
import android.view.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.maps.*
import com.ujjallamichhane.ridesharing.NotificationChannels
import com.ujjallamichhane.ridesharing.SignInActivity


class MapsFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private lateinit var btnCurrentLocation: FloatingActionButton
    private lateinit var etSetOnMap: AutoCompleteTextView
    private lateinit var etSetLocation: EditText
    private lateinit var tvPrice: TextView
    private lateinit var btnConfirm: Button
    private lateinit var btnRequest: Button
    private lateinit var btnInvite: Button
    private lateinit var btnCancel: Button
    private lateinit var btnJoin: Button
    private lateinit var btnDecline: Button
    private lateinit var lollipop: ImageView
    private lateinit var imgDriver: ImageView
    private lateinit var imgSender: CircleImageView
    private lateinit var tvDriversName: TextView
    private lateinit var tvSendersName: TextView
    private lateinit var tvPickupLocation: TextView
    private lateinit var tvDestination: TextView
    private lateinit var tvJoinedCustomer: TextView
    private lateinit var imgRating: ImageView
    private lateinit var tvPhone: TextView
    private lateinit var tvFare: TextView
    private lateinit var tvCarNo: TextView
    private lateinit var tvColor: TextView
    private lateinit var toggle: RadioGroup
    private lateinit var off: RadioButton
    private lateinit var on: RadioButton

    lateinit var mSocket: Socket;
    var gson: Gson = Gson();

    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    var geo: Geocoder? = null

    private var currentMarker: Marker? = null
    private var currentDialog: Dialog? = null

    private var addresses: List<Address>? = null
    private var destination: String = ""
    private var pickUp: String = ""

    private var currentLocation: LatLng = LatLng(20.5, 78.9)
    private var cameraPos: LatLng = LatLng(0.0, 0.0)
    private var polylines: ArrayList<Polyline>? = null

    private var apiKey: String = ""
    private var distance: Double = 0.0

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
        val view = inflater.inflate(R.layout.fragment_customer_maps, container, false)
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)
        etSetOnMap = view.findViewById(R.id.etHello)
        lollipop = view.findViewById(R.id.lollipop)
        toggle = view.findViewById(R.id.toggle)
        off = view.findViewById(R.id.off)
        on = view.findViewById(R.id.on)

        //get current location and move camera
        btnCurrentLocation.setOnClickListener {
            getLocation()
        }

        findRide()

        //set destination on map
        etSetOnMap.setOnClickListener {
            any()
            lollipop.isVisible = true
        }

        val ai: ApplicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }

        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        ServiceBuilder.setSocket()
        ServiceBuilder.establishConnection()
        mSocket = ServiceBuilder.getSocket()

//        Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
        mSocket.on("accepted" + ServiceBuilder.customer!!._id.toString(), requestAccept)
        mSocket.on("ridejoined" + ServiceBuilder.customer!!._id.toString(), connectedPassengers)
        mSocket.on("drCanceled" + ServiceBuilder.customer!!._id.toString(), showNotification)
        mSocket.on("driverCanceled" + ServiceBuilder.customer!!._id.toString(), showNotification)

        return view
    }

    //get current location as per the device's location
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

                        try {
                            geo = Geocoder(requireContext(), Locale.getDefault())
                            addresses = geo!!.getFromLocation(
                                currentLocation.latitude,
                                currentLocation.longitude,
                                1
                            )
                            pickUp = addresses!![0].getAddressLine(0)

                        } catch (e: IOException) {
                            Toast.makeText(
                                requireContext(),
                                "$e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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


    fun any() {
        mMap.setOnCameraMoveListener {
            val position = mMap.cameraPosition.target
            if (currentMarker == null) {
                currentMarker = mMap.addMarker(
                    MarkerOptions().position(position).visible(false).title(destination)
//                        .icon(BitmapFromVector(requireContext(), R.drawable.ic_lollipop))
                )
                currentMarker!!.showInfoWindow()

            } else {
                currentMarker!!.position = position
                cameraPos = LatLng(position.latitude, position.longitude)
            }
        }

        mMap.setOnCameraIdleListener {
            geo = Geocoder(requireContext(), Locale.getDefault())

            try {
                addresses = geo!!.getFromLocation(cameraPos.latitude, cameraPos.longitude, 2)
                destination = addresses!![0].getAddressLine(0)
                confirmDestination(destination)

            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    "$e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //bottom sheet for destination confirmation
    private fun confirmDestination(destination: String) {
        if (currentDialog == null) {
            currentDialog = BottomSheetDialog(requireContext())
            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            etSetLocation = view.findViewById(R.id.etSetLocation)
            btnConfirm = view.findViewById(R.id.btnConfirm)

            etSetLocation.setText(destination)

            currentDialog!!.setContentView(view)
            currentDialog!!.show()

            btnConfirm.setOnClickListener {
                currentDialog!!.dismiss()

                //finds shortest route and draws a route on map
                findroutes(currentLocation, cameraPos)
            }
        } else {
            currentDialog = null
            confirmDestination(destination)
        }
    }

    // function to find Routes
    private fun findroutes(Start: LatLng?, End: LatLng?) {
        if (Start == null || End == null) {
            Toast.makeText(context, "Unable to get location", Toast.LENGTH_LONG).show()
        } else {
            val routing: Routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(Start, End)
                .key("AIzaSyCfDFflWpAZ96jzSn3bcIGsxsl_r0LsC10") //also define your api key here.
                .build()
            routing.execute()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        findroutes(currentLocation, cameraPos);
    }

    override fun onRoutingFailure(e: RouteException) {
        Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onRoutingStart() {
        currentDialog = ProgressDialog.show(
            context, "",
            "Finding shortest route. Please wait...",
            true
        )
    }

    override fun onRoutingCancelled() {
        findroutes(currentLocation, cameraPos)
    }

    @SuppressLint("ResourceType")
    override fun onRoutingSuccess(route: ArrayList<Route>?, shortestRouteIndex: Int) {
        polylines = ArrayList()
        polylines!!.clear()
        val polyOptions = PolylineOptions()

        //add route(s) to the map using polyline
        for (i in 0 until route!!.size) {
            if (i == shortestRouteIndex) {
                polyOptions.color(Color.BLUE)
                polyOptions.width(7f)
                polyOptions.addAll(route[shortestRouteIndex].points)
                val polyline = mMap.addPolyline(polyOptions)
                currentLocation = polyline.points[0]
                val k = polyline.points.size
                cameraPos = polyline.points[k - 1]
                polylines!!.add(polyline)

                distance = SphericalUtil.computeLength(polyline.points) / 1000

                currentDialog?.dismiss()
                sendRequestBottomSheet()
            } else {

            }
        }
    }

    val currentDateTimeString = DateFormat
        .getDateTimeInstance()
        .format(Date())

    //bottom sheet for sending ride request after confirming destination and route
    //and viewing price and distance
    private fun sendRequestBottomSheet() {
        currentDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.send_request, null)
        tvPrice = view.findViewById(R.id.tvPrice)
        btnRequest = view.findViewById(R.id.btnRequest)
        tvPrice.text = distance.toString()
        currentDialog!!.setContentView(view)
        currentDialog!!.show()

        // The following lines connects the Android app to the server.
        ServiceBuilder.setSocket()
        ServiceBuilder.establishConnection()
        val mSocket = ServiceBuilder.getSocket()

        btnRequest.setOnClickListener {

            val data = gson.toJson(
                RideRequest(
                    from = pickUp,
                    to = destination,
                    date = currentDateTimeString,
                    distance = distance.toString(),
                    price = "200",
                    customer = ServiceBuilder.customer,
                    customerID = ServiceBuilder.customer!!._id
                )
            )
            mSocket.emit("message", data)
            currentDialog!!.dismiss()
            currentDialog = null

            currentDialog = ProgressDialog.show(
                context, "Ride Sharing",
                "Searching for the ride. Please wait...",
                true
            )
        }
    }

    private var requestAccept = Emitter.Listener {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val counter = it[0] as JSONObject
                val data = gson.fromJson(counter.toString(), Driver::class.java)

                withContext(Dispatchers.Main) {
                    currentDialog!!.dismiss()
                    currentDialog = null
                    if (currentDialog == null) {
                        currentDialog = BottomSheetDialog(requireContext())
                        val view = layoutInflater.inflate(R.layout.invite_passengers, null)
                        currentDialog!!.setContentView(view)
                        currentDialog!!.show()

                        imgDriver = view.findViewById(R.id.imgDriver)
                        imgRating = view.findViewById(R.id.imgRating)
                        tvDriversName = view.findViewById(R.id.tvDriversNme)
                        tvFare = view.findViewById(R.id.tvFare)
                        tvPhone = view.findViewById(R.id.tvPhone)
                        tvCarNo = view.findViewById(R.id.tvCarNo)
                        tvColor = view.findViewById(R.id.tvColor)
                        btnInvite = view.findViewById(R.id.btnInvite)
                        btnCancel = view.findViewById(R.id.btnCancel)
//                                    Log.d("Request Ride", data.toString())
                        tvDriversName.text = data.fullname
                        tvPhone.text = data.phone
                        tvCarNo.text = data.vechileNo
                        tvColor.text = data.model
                        Glide.with(requireContext())
                            .load(ServiceBuilder.BASE_URL + data.photo)
                            .into(imgDriver)
//
//                        Toast.makeText(context, "$data", Toast.LENGTH_LONG).show()

                        btnCancel.setOnClickListener {
                            val message = "Customer has cancelled the ride"
                            mSocket.emit("customerCancel", message)
                            currentDialog!!.dismiss()
                        }

                        btnInvite.setOnClickListener {

                            currentDialog!!.dismiss()
                            val data1 = gson.toJson(
                                RideRequest(
                                    customer = ServiceBuilder.customer,
                                    from = pickUp,
                                    to = destination,
                                    date = currentDateTimeString,
                                    distance = distance.toString(),
                                    price = "200",
                                    driver = data
                                )
                            )
                            mSocket.emit("invite", data1)

                            currentDialog = ProgressDialog(context)
                            (currentDialog as ProgressDialog).setMessage("Waiting for other passengers to join in the ride...")
                            (currentDialog as ProgressDialog).setButton(
                                DialogInterface.BUTTON_NEGATIVE, "Cancel"
                            ) { dialog, which -> dialog.dismiss() }
                            (currentDialog as ProgressDialog).setButton(
                                DialogInterface.BUTTON_POSITIVE, "Ready to go"
                            ) { dialog, which ->
//                                mSocket.on("ridejoined" + ServiceBuilder.customer!!._id.toString(), connectedPassengers)
                            }
                            currentDialog!!.show()
                        }

                    } else {
                        currentDialog = null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var showNotification = Emitter.Listener {
        val counter = it[0]
        val notificationManager = NotificationManagerCompat.from(requireContext())

        val notificationChannels = NotificationChannels(requireContext())
        notificationChannels.createNotificationChannels()

        val notification =
            NotificationCompat.Builder(requireContext(), notificationChannels.CHANNEL_1)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle("Ride Sharing")
                .setContentText(counter.toString())
                .setColor(Color.GREEN)
                .build()

        currentDialog!!.dismiss()
        currentDialog = null;

        notificationManager.notify(1, notification)

    }

    private fun findRide() {
        toggle.setOnCheckedChangeListener { toggle, checkedId ->
            when (checkedId) {
                R.id.on -> {
                    Toast.makeText(context, "hello asdf", Toast.LENGTH_SHORT).show()
                    getInvite()
                }
                R.id.off -> {
//                    gender = rbFemale.text.toString()
                }
            }
        }
    }

    private fun getInvite() {
//        if (ServiceBuilder.customer != null) {
        mSocket.on("customer_" + ServiceBuilder.customer!!._id.toString()) { args ->
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    if (args[0] != null) {
                        val counter = args[0] as JSONObject
                        val gson = Gson()
                        val data = gson.fromJson(counter.toString(), RideRequest::class.java)

                        withContext(Dispatchers.Main) {
                            if (currentDialog == null) {
                                Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
                                currentDialog = BottomSheetDialog(requireContext())
                                val view =
                                    layoutInflater.inflate(R.layout.available_rides, null)
                                currentDialog!!.setContentView(view)
                                currentDialog!!.show()

                                tvSendersName = view.findViewById(R.id.tvSendersName)
                                tvPickupLocation = view.findViewById(R.id.tvPickUpLocation)
                                tvDriversName = view.findViewById(R.id.tvDriversName)
                                imgDriver = view.findViewById(R.id.imgDriver)
                                tvDestination = view.findViewById(R.id.tvDestination)
                                tvFare = view.findViewById(R.id.tvFare)
                                imgSender = view.findViewById(R.id.imgSender)
                                btnJoin = view.findViewById(R.id.btnJoin)
                                btnDecline = view.findViewById(R.id.btnDecline)

                                tvSendersName.text = data.customer!!.fullname
                                tvPickupLocation.text = data.from
                                tvDestination.text = data.to
                                tvDriversName.text = data.driver!!.fullname
                                Glide.with(requireContext())
                                    .load(ServiceBuilder.BASE_URL + data.customer.photo)
                                    .into(imgSender)

                                Glide.with(requireContext())
                                    .load(ServiceBuilder.BASE_URL + data.driver.photo)
                                    .into(imgDriver)
                                btnJoin.setOnClickListener {
                                    val data2 = gson.toJson(
                                        RideRequest(
                                            customer = ServiceBuilder.customer,
                                            driver = data.driver
                                        )
                                    )
                                    mSocket.emit("join", data2)
                                    currentDialog!!.dismiss()
                                }

                                btnDecline.setOnClickListener {
                                    currentDialog!!.dismiss()
                                    currentDialog = null;
                                }

                            } else {
                                currentDialog = null
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            }
        }
    }

    private var connectedPassengers = Emitter.Listener {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val counter = it[0] as JSONObject
                val data = gson.fromJson(counter.toString(), RideRequest::class.java)

                withContext(Dispatchers.Main) {
                    currentDialog!!.dismiss()
                    currentDialog = null
                    if (currentDialog == null) {
                        Toast.makeText(context, "$data", Toast.LENGTH_SHORT).show()
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("${data.customer!!.fullname} joined the ride")
                        builder.setIcon(R.drawable.ic_info)
                        builder.setPositiveButton("Ready to go") { _, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    if (currentDialog == null) {
                                        currentDialog = BottomSheetDialog(requireContext())
                                        val view =
                                            layoutInflater.inflate(R.layout.start_ride, null)
                                        currentDialog!!.setContentView(view)
                                        currentDialog!!.show()

                                        imgDriver = view.findViewById(R.id.imgDriver)
                                        tvDriversName = view.findViewById(R.id.tvDriversName)
                                        tvJoinedCustomer = view.findViewById(R.id.tvJoinedCustomer)

                                        tvJoinedCustomer.text = data.customer.fullname
                                        tvDriversName.text = data.driver!!.fullname

                                        Glide.with(requireContext())
                                            .load(ServiceBuilder.BASE_URL + data.driver.photo)
                                            .into(imgDriver)
                                    }
                                }
                            }
                        }

                        builder.setNegativeButton("Wait") { _, _ ->

                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(true)
                        alertDialog.show()
                    } else {
                        currentDialog = null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}


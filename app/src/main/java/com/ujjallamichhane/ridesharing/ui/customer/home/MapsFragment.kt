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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.directions.route.*
import com.directions.route.Route
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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


class MapsFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private lateinit var btnCurrentLocation: FloatingActionButton
    private lateinit var etSetOnMap: EditText
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
    private lateinit var imgRating: ImageView
    private lateinit var tvPhone: TextView
    private lateinit var tvFare: TextView
    private lateinit var tvCarNo: TextView
    private lateinit var tvColor: TextView
    private lateinit var group: RadioGroup
    private lateinit var search: RadioButton
    private lateinit var offer: RadioButton

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
        group = view.findViewById(R.id.group)
        search = view.findViewById(R.id.search)
        offer = view.findViewById(R.id.offer)

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
                    fullname = ServiceBuilder.customer!!.fullname,
                    contact = ServiceBuilder.customer!!.contact,
                    from = pickUp,
                    to = destination,
                    date = currentDateTimeString,
                    distance = distance.toString(),
                    price = "200",
                    customerID = ServiceBuilder.customer!!._id.toString(),
                    photo = ServiceBuilder.customer!!.photo
                )
            )
            mSocket.emit("message", data)
            currentDialog!!.dismiss()
            currentDialog = null
        }
    }

    private var requestAccept = Emitter.Listener {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val counter = it[0] as JSONObject
                val data = gson.fromJson(counter.toString(), Driver::class.java)

                withContext(Dispatchers.Main) {
//                                Log.d("Request Ride", "True")
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
                            .load(ServiceBuilder.BASE_URL+data.photo)
                            .into(imgDriver)

                        Toast.makeText(context, "$data", Toast.LENGTH_LONG).show()

                        btnInvite.setOnClickListener {

                            currentDialog!!.dismiss()
                            val data = gson.toJson(
                                RideRequest(
                                    fullname = ServiceBuilder.customer!!.fullname,
                                    contact = ServiceBuilder.customer!!.contact,
                                    from = pickUp,
                                    to = destination,
                                    date = currentDateTimeString,
                                    distance = distance.toString(),
                                    price = "200"
                                )
                            )
                            mSocket.emit("invite", data)

                            val myDialog = ProgressDialog(context)
                            myDialog.setMessage("Waiting for other passengers to join in the ride...")
                            myDialog.setButton(
                                DialogInterface.BUTTON_NEGATIVE, "Cancel"
                            ) { dialog, which -> dialog.dismiss() }
                            myDialog.setButton(
                                DialogInterface.BUTTON_POSITIVE, "Ready to go"
                            ) { dialog, which ->
                                // your code
                            }
                            myDialog.show()
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

    private fun findRide() {
        group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.offer -> {
                    mSocket.on("invite", getInvite)
                    Toast.makeText(context, "hello asdf", Toast.LENGTH_SHORT).show()

                }
//                R.id.rbFemale->{
//                    gender = rbFemale.text.toString()
//                }
            }
        }
    }

    private var getInvite = Emitter.Listener {
        if (ServiceBuilder.customer != null) {
            mSocket.on("customer_" + ServiceBuilder.customer!!._id.toString()) { args ->
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (args[0] != null) {
                            val counter = args[0] as JSONObject
                            val gson = Gson()
                            val data = gson.fromJson(counter.toString(), RideRequest::class.java)

                            withContext(Dispatchers.Main) {
                                if (currentDialog == null) {
                                    currentDialog = BottomSheetDialog(requireContext())
                                    val view =
                                        layoutInflater.inflate(R.layout.available_rides, null)
                                    currentDialog!!.setContentView(view)
                                    currentDialog!!.show()

                                    tvSendersName = view.findViewById(R.id.tvSendersName)
                                    tvPickupLocation = view.findViewById(R.id.tvPickUpLocation)
                                    tvDestination = view.findViewById(R.id.tvDestination)
                                    tvFare = view.findViewById(R.id.tvFare)
                                    imgSender = view.findViewById(R.id.imgSender)
                                    btnJoin = view.findViewById(R.id.btnJoin)
                                    btnDecline = view.findViewById(R.id.btnDecline)

                                    tvSendersName.text = data.fullname
                                    tvPickupLocation.text = data.from
                                    tvDestination.text = data.to
                                    btnJoin.setOnClickListener {

                                        currentDialog!!.dismiss()
                                        val data = gson.toJson(
                                            Customer(
                                                fullname = ServiceBuilder.customer!!.fullname,
                                                contact = ServiceBuilder.customer!!.contact,
                                            )
                                        )
                                        mSocket.emit("joined", data)
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
            }
        }
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}


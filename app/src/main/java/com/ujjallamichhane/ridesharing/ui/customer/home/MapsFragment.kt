package com.ujjallamichhane.ridesharing.ui.customer.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
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


class MapsFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private lateinit var btnCurrentLocation: FloatingActionButton
    private lateinit var etSetOnMap: EditText
    private lateinit var etSetLocation: EditText
    private lateinit var tvPrice: TextView
    private lateinit var btnConfirm: Button
    private lateinit var btnRequest: Button
    private lateinit var lollipop: ImageView

    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    var geo: Geocoder? = null

    private var currentMarker: Marker? = null
    private var currentDialog: Dialog? = null

    private var addresses: List<Address>? = null
    private var destination: String = ""

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

        //get current location and move camera
        btnCurrentLocation.setOnClickListener {
            getLocation()
        }

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
                addresses = geo!!.getFromLocation(cameraPos.latitude, cameraPos.longitude, 1)
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

                distance = SphericalUtil.computeLength(polyline.points)

                currentDialog?.dismiss()
                sendRequestBottomSheet()
            } else {
            }
        }
    }

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

        btnRequest.setOnClickListener {

            val client = OkHttpClient()
            val request: Request = Request.Builder().url("ws://10.0.2.2:90/").build()

            val listener = EchoWebSocketListener()

            var ws: WebSocket = client.newWebSocket(request, listener)
            client.dispatcher().executorService().shutdown()

//            currentDialog = null
//            requestAcceptedBottomSheet()
        }
    }

    private fun requestAcceptedBottomSheet() {
        if (currentDialog == null) {
            currentDialog = BottomSheetDialog(requireContext())
            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.invite_passengers, null)
            currentDialog!!.setContentView(view)
            currentDialog!!.show()

        } else {
            currentDialog = null
        }
    }

    class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response?) {
            val rideRequest = RideRequest(
                fullname = "Ujjal", phone = "0987", from = "Here", to = "There",
                date = "today", time = "now", distance = "100", price = "200"
            ).toString()

            val fullname: String = "Ujjal"

            val ride = JSONObject("""{"userType" = "customer","fullname" = "$fullname", "phone" = "0987", "from" = "Here", "to" = "There",
                "date" = "today", "time" = "now", "distance" = "100", "price" = "200"}""").toString()

            webSocket.send(ride)
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Receiving: $text")
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            println("Receiving: " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            println("Closing: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
            t.printStackTrace()
        }

        companion object {
            private const val NORMAL_CLOSURE_STATUS = 1000
        }
    }
}

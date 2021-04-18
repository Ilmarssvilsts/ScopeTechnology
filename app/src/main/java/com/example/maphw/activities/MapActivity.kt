package com.example.maphw.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.example.maphw.*
import com.example.maphw.BuildConfig.MAPS_API_KEY
import com.example.maphw.R
import com.example.maphw.adapters.UsersAdapter
import com.example.maphw.api.API
import com.example.maphw.api.models.*
import com.example.maphw.data.Vehicle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    var vehicleList: MutableList<VehicleLocation> = mutableListOf()
    private lateinit var map: GoogleMap
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var id: Int = 0
    private var vehicles: MutableList<Vehicle> = mutableListOf()
    lateinit var mainHandler: Handler

    private val vehicleViewModel: VehicleViewModel by viewModels {
        VehicleViewModelFactory((application.getApplicationContext() as MapApplication).vehicleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        id = intent.getIntExtra("id", 0)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (isNetworkConnected()) {
            mainHandler = Handler(Looper.getMainLooper())
            vehicleViewModel.allVehicles.observe(owner = this) { words ->
                // Update the cached copy of the words in the adapter.
                words.let {
                    vehicles = it as MutableList<Vehicle>
                    map.setInfoWindowAdapter(InfoWindow(this, vehicles))
                }
            }
        } else {
            finish()
            Toast.makeText(baseContext, getString(R.string.no_network), Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
    }

    private fun onFailure(t: Throwable) {
        Toast.makeText(baseContext, getString(R.string.error), Toast.LENGTH_SHORT).show()
        if (isNetworkConnected()) {
            mainHandler.removeCallbacks(updateAPI)
            mainHandler.post(updateAPI)
        }
    }

    private fun onResponse(response: VehicleLocationList) {
        vehicleList = response.data as MutableList<VehicleLocation>

        val geocoder: Geocoder
        var addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())


                for (item in vehicleList) {
            var lat = item.lat?.toDoubleOrNull()
            var lon = item.lon?.toDoubleOrNull()
            if (lat != null && lon != null) {
                addresses = geocoder.getFromLocation(lat, lon,1)
                map.apply {

                    val location = LatLng(lat, lon)
                    addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(item.vehicleId.toString())
                                .snippet(addresses.get(0).getAddressLine(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.small_vehicle_icon))
                    ).tag = item.vehicleId
                }
            }
        }
    }

    private fun getRouteData(startLatLng: String, endLatLng: String) {
        API.buildRouteApi().getRoute(startLatLng, MAPS_API_KEY, endLatLng)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onRouteResponse(response) }, { t -> onRouteFailure(t) })
    }

    private fun onRouteResponse(response: Route) {
    }

    private fun onRouteFailure(t: Throwable) {
    }

    private fun getData() {
        API.buildApi().getCurrentPosition(id.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
    }

    private val updateAPI = object : Runnable {
        override fun run() {
            getData()
            mainHandler.postDelayed(this, 60000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateAPI)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateAPI)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        for (item in permissions.indices) {
            if (Manifest.permission.ACCESS_FINE_LOCATION == permissions[item]) {
                if (grantResults[item] == PackageManager.PERMISSION_DENIED) {
                    finish()
                } else {
                    updateLocationUI()
                    getDeviceLocation()
                }
            }
        }
    }

    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map.isMyLocationEnabled = false
                map.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        map.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    //todo should change methods and not use deprecated ones.
    //todo add this to utils class
    private fun isNetworkConnected(): Boolean {
        val cm = baseContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
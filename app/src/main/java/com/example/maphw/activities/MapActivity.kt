package com.example.maphw.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.maphw.BuildConfig.MAPS_API_KEY
import com.example.maphw.R
import com.example.maphw.api.API
import com.example.maphw.api.models.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    var vehicleList: MutableList<VehicleLocation> = mutableListOf()
    private lateinit var map: GoogleMap
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (isNetworkConnected()) {
            getData()
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
            getData()
        }
    }

    private fun onResponse(response: VehicleLocationList) {
        vehicleList = response.data as MutableList<VehicleLocation>

        for (item in vehicleList) {
            var lat = item.lat?.toDoubleOrNull()
            var lon = item.lon?.toDoubleOrNull()
            if (lat != null && lon != null) {
                map.apply {

                    val sydney = LatLng(lat, lon)
                    addMarker(
                        MarkerOptions()
                            .position(sydney)
                            .title(item.vehicleId.toString())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.small_vehicle_icon))
                    )
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
        val id: String = intent.getStringExtra("id").toString()
        API.buildApi().getCurrentPosition(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
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
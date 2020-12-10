package tech.ru1t3rl.madcapstoneproject.ui

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.databinding.ActivityMapsBinding
import java.io.IOException
import java.lang.NullPointerException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager
    private val REQUEST_LOCATION_PERMISSION = 1

    // Location Tracking Variables
    private var routePonits = ArrayList<LatLng>()
    private val minMovement = 0.00000002f
    private var tracking = false
    private var setStartLocation = false
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if location service is enabled
        if (!isLocationEnabled())
            Toast.makeText(
                applicationContext,
                getString(R.string.warning_enable_location),
                Toast.LENGTH_LONG
            ).show()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager =
            this.getSystemService(Service.LOCATION_SERVICE) as LocationManager

        // The popup with all user available content
        binding.btnOpen.setOnClickListener {
            if (binding.popup.visibility == View.GONE) {
                binding.popup.visibility = (View.VISIBLE)
                binding.btnOpen.rotationX = (180f)
            } else {
                binding.popup.visibility = (View.GONE)
                binding.btnOpen.rotationX = (0f)
            }
        }

        // Setup the start button
        binding.floatingActionButton.setOnClickListener {
            if (!isLocationEnabled()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.warning_enable_location),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            tracking = !tracking

            if (tracking) {
                routePonits.clear()

                try {
                    routePonits.add(currentLocation!!)
                } catch (ex: NullPointerException) {

                }

                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.stop_icon
                    )
                )


                binding.timer.base = SystemClock.elapsedRealtime()
                binding.timer.start()
            } else {
                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        android.R.drawable.ic_media_play
                    )
                )

                binding.timer.stop()
                // TODO: Upload data to database
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Uncomment for dynamically changing map color based on android theme
        val currentNightMode =
            applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        applicationContext, R.raw.light_map_style
                    )
                )
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        applicationContext, R.raw.dark_map_style
                    )
                )
            }
        }

        setupMap()
    }

    private fun setupMap() {
        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            // Setup the locationListener
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude: Double = location.latitude
                    val longitude: Double = location.longitude

                    currentLocation = LatLng(latitude, longitude)

                    if (!setStartLocation) {
                        try {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentLocation,
                                    19.0f
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        setStartLocation = true
                    }

                    // Detect Movement and save route
                    if (tracking) {
                        // Move the camera to the currentLocation
                        try {
                            val latLng = LatLng(latitude, longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.0f))
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        if (routePonits.size > 0) {
                            val x = routePonits[routePonits.size - 1].latitude - latitude
                            val y = routePonits[routePonits.size - 1].longitude - longitude
                            val distance = x * x + y * y

                            if (distance >= minMovement)
                                routePonits.add(LatLng(latitude, longitude))
                        }

                        if (routePonits.size > 1)
                            mMap
                                .addPolyline(
                                    PolylineOptions()
                                        .add(
                                            routePonits[routePonits.size - 1],
                                            routePonits[routePonits.size - 2]
                                        ).width(5f).color(getColor(R.color.background_accent))
                                        .geodesic(true)
                                )
                    }
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            // Link both the network and gps provider to locationUpdates
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )

            mMap.isMyLocationEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        setupMap()
    }
}
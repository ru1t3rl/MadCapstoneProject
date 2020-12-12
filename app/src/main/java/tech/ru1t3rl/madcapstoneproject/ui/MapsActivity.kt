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
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.databinding.ActivityMapBinding
import tech.ru1t3rl.madcapstoneproject.model.Run
import tech.ru1t3rl.madcapstoneproject.viewmodel.RunModel
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel
import java.io.IOException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

const val ARG_ACTIVE_RUN = "ARG_ACTIVE_RUN"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager
    private val REQUEST_LOCATION_PERMISSION = 1

    // Location Tracking Variables
    private val minMovement = 0.00000002f
    private var tracking = false
    private var setStartLocation = false
    private var currentLocation: LatLng? = null

    val startPoint = Location("startLocation")
    val endPoint = Location("endLocation")
    private var distance = 0f

    var activeRun: Run? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get All Users for finding friends
        UserModel.getAllUsers()

        // Check if location service is enabled
        if (!isLocationEnabled())
            Toast.makeText(
                applicationContext,
                getString(R.string.warning_enable_location),
                Toast.LENGTH_LONG
            ).show()

        binding = ActivityMapBinding.inflate(layoutInflater)
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
                binding.fabStart.visibility = View.GONE
                binding.popup.visibility = View.VISIBLE
                binding.btnOpen.rotation = 180f
            } else {
                binding.fabStart.visibility = View.VISIBLE
                binding.popup.visibility = View.GONE
                binding.btnOpen.rotation = 0f
            }
        }

        handler = Handler()

        // Setup the start button
        binding.fabStart.setOnClickListener {
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
                binding.fabStart.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.stop_icon
                    )
                )

                // Reset the stopwatch
                millisecondTime = 0L
                startTime = 0L
                timeBuff = 0L
                updateTime = 0L
                seconds = 0
                minutes = 0
                milliSeconds = 0

                // Start the stopwatch
                startTime = SystemClock.uptimeMillis()
                handler!!.postDelayed(runnable, 0)

                activeRun = Run(null)

                // Add start location to the route
                try {
                    activeRun!!.routePoints.add(currentLocation!!)
                } catch (ex: NullPointerException) {

                }
            } else {
                binding.fabStart.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        android.R.drawable.ic_media_play
                    )
                )

                // Stop the timer
                timeBuff += millisecondTime
                handler!!.removeCallbacks(runnable)

                // V = S / T (totalTime/1000/60/60) to convert the milliseconds to hours
                activeRun!!.averageSpeed = (activeRun!!.distance.toFloat() / (activeRun!!.time/1000/60/60)).toString()

                activeRun!!.score = (activeRun!!.distance.toFloat() * (activeRun!!.time/1000/60/60) * activeRun!!.averageSpeed.toFloat()).toInt()

                RunModel.addRun(activeRun!!)
                activeRun = null
            }
        }

        setNavigation(findNavController(R.id.nav_host_fragment))
    }

    private fun setNavigation(navController: NavController) {
        val navView: BottomNavigationView = binding.bottomNav

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.miStats -> {
                    var args = Bundle()
                    args.putSerializable("ARG_ACTIVE_RUN", activeRun)
                    navController.navigate(R.id.statsFragment)
                }
                R.id.miFriends -> {
                    navController.navigate(R.id.findFriendsFragments)
                }
                R.id.miLeaderboard -> {
                    navController.navigate(R.id.leaderboardFragment)
                }
                R.id.miProfile -> {
                    navController.navigate(R.id.settingsFragment)
                }
            }
            true
        }
    }

    // Check if location service is enabled
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Dynamically changing map color based on android theme
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

                        if (activeRun!!.routePoints.size > 0) {
                            val x = activeRun!!.routePoints[activeRun!!.routePoints.size - 1].latitude - latitude
                            val y = activeRun!!.routePoints[activeRun!!.routePoints.size - 1].longitude - longitude
                            val distance = x * x + y * y

                            if (distance >= minMovement)
                                activeRun!!.routePoints.add(LatLng(latitude, longitude))
                        }

                        if (activeRun!!.routePoints.size > 1) {
                            mMap
                                .addPolyline(
                                    PolylineOptions()
                                        .add(
                                            activeRun!!.routePoints[activeRun!!.routePoints.size - 1],
                                            activeRun!!.routePoints[activeRun!!.routePoints.size - 2]
                                        ).width(5f).color(getColor(R.color.background_accent))
                                        .geodesic(true)
                                )

                            startPoint.latitude = activeRun!!.routePoints[activeRun!!.routePoints.size - 2].latitude
                            startPoint.longitude = activeRun!!.routePoints[activeRun!!.routePoints.size - 2].longitude

                            endPoint.latitude = latitude
                            startPoint.longitude = longitude

                            // DistanceTo / 1000 to change it to km instead of m
                            distance += startPoint.distanceTo(endPoint)/1000
                            activeRun!!.distance = distance.toString()
                        }
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


    // Stopwatch Code
    var handler: Handler? = null
    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0L
    var milliSeconds = 0
    var seconds = 0
    var minutes = 0
    var hours = 0
    var runnable: Runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            minutes = seconds / 60
            hours = minutes /60
            milliSeconds = (updateTime % 1000).toInt()
            seconds %= 60
            binding.timer.text = ("${hours}:${minutes}:${seconds}.${milliSeconds}")
            activeRun!!.time = updateTime.toInt()
            handler!!.postDelayed(this, 0)
        }
    }
}
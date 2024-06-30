package com.dutaram.intermediatesm1.view.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dutaram.intermediatesm1.R
import com.dutaram.intermediatesm1.ViewModelFactory
import com.dutaram.intermediatesm1.api.response.ListStoryItem
import com.dutaram.intermediatesm1.data.pref.UserPreference
import com.dutaram.intermediatesm1.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        getMyLocation()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun setupView() {
        val actionBar = supportActionBar
        actionBar?.title = "All Stories Location"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun setupViewModel() {
        mapViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[MapViewModel::class.java]

        mapViewModel.getUser().observe(this) { user ->
            mapViewModel.getAllStoriesWithLocation("Bearer " + user.token)
        }

        mapViewModel.location.observe(this) { stories ->
            setLocation(stories)
        }
    }


    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style: ", exception)
        }
    }

    private fun setLocation(stories: List<ListStoryItem>) {
        stories.forEach { story ->
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                val addressName = getAddressName(story.lat, story.lon)
                val markerOptions =
                    MarkerOptions()
                        .position(latLng)
                        .title("Uploaded by " + story.name)
                        .snippet(addressName)
                mMap.addMarker(
                    markerOptions
                )
            }
        }

        val indonesia = LatLng(-0.7893, 118.9213)
        val zoomLevel = 3.5f
        val cameraPosition = CameraPosition.Builder().target(indonesia).zoom(zoomLevel).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    private fun getAddressName(lat: Double, lon: Double): String? {
        if (!GeoLocation.coordinatesValid(lat, lon)) {
            throw IllegalArgumentException("Not a valid geo location: $lat, $lon")
        }

        var addressName: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.isNotEmpty()) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error getting address: ${e.message}")
        }

        return addressName
    }

    object GeoLocation {
        fun coordinatesValid(latitude: Double, longitude: Double): Boolean {
            return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180
        }
    }


    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Presice location is not enabled.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

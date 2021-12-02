package com.pns.albang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.pns.albang.databinding.ActivityMainBinding
import com.pns.albang.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationSource: FusedLocationSource
    private lateinit var circleOverlay: CircleOverlay
    private val landmarkInfoWindow = InfoWindow()

    private val onMapReadyCallback = OnMapReadyCallback {
        this.naverMap = it.apply {
            val uiSettings = this.uiSettings
            uiSettings.apply {
                isCompassEnabled = false
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                isLocationButtonEnabled = false
                logoGravity = Gravity.TOP
                logoGravity = Gravity.END
            }
            binding.mapLocation.map = this
            binding.mapZoomControl.map = this

            this.moveCamera(CameraUpdate.zoomTo(16.0))

            locationSource = fusedLocationSource
            locationTrackingMode = LocationTrackingMode.Follow

            circleOverlay = CircleOverlay(locationOverlay.position, 50.0)
            circleOverlay.map = this

            locationOverlay.circleRadius = 0
            locationOverlay.icon = OverlayImage.fromResource(R.drawable.ic_map_location)
            locationOverlay.isVisible = true

            this.addOnLocationChangeListener { location ->
                Log.d(TAG, "location : ${location.latitude}, ${location.longitude}")
                circleOverlay.center = LatLng(location)
                circleOverlay.radius = 50.0
                circleOverlay.outlineWidth = 0
                circleOverlay.color = getColor(R.color.locationOverlayColor)

                viewModel.checkInBoundLandmark(circleOverlay.bounds)
            }

            this.setOnMapClickListener { _, _ ->
                landmarkInfoWindow.close()
            }
        }

        setObserver()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (fusedLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!fusedLocationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }

            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        fusedLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map) as MapFragment

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun setObserver() {
        viewModel.landmarks.observe(this) {
            it.forEach { landmark ->
                landmark.mapMarker.map = naverMap
                landmarkInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this@MainActivity) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return infoWindow.marker?.tag as CharSequence? ?: ""
                    }
                }
                landmark.mapMarker.setOnClickListener { overlay ->
                    val marker = overlay as Marker

                    if (marker.infoWindow == null) {
                        landmarkInfoWindow.open(marker)
                    } else {
                        landmarkInfoWindow.close()
                    }

                    true
                }
            }
        }

        viewModel.targetLandmark.observe(this) {
            val boundedLandmark = it.getContentIfNotHandled()
            val intent = if (boundedLandmark == null) {
                Intent(this, TestActivity::class.java)
            } else {
                Intent(this, ReviewActivity::class.java).apply {
                    putExtra("landmark", boundedLandmark.id)
                    putExtra("landmarkImage", boundedLandmark.imageName)
                    putExtra("landmarkName", boundedLandmark.name)
                }
            }

            if (boundedLandmark == null) {
                binding.btnBottom.icon = ContextCompat.getDrawable(this, R.drawable.ic_btn_request)
                binding.btnBottom.text = getString(R.string.btn_request)
            } else {
                binding.btnBottom.icon = ContextCompat.getDrawable(this, R.drawable.ic_guestbook)
                binding.btnBottom.text = getString(R.string.btn_show_guestbook)
            }

            binding.btnBottom.setOnClickListener {
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val TAG = "MAIN ACTIVITY"

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
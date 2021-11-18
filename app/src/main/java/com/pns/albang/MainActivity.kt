package com.pns.albang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.pns.albang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationSource: FusedLocationSource
    private lateinit var circleOverlay: CircleOverlay

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
            }
        }
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

    companion object {
        private const val TAG = "MAIN ACTIVITY"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
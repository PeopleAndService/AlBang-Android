package com.pns.albang.data

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

data class Landmark(

    val id: Long,
    val name: String,
    val imageName: String,
    val coordinate: LatLng,
    val mapMarker: Marker
)

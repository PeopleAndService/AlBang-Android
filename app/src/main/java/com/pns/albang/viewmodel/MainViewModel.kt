package com.pns.albang.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.pns.albang.R
import com.pns.albang.data.Landmark
import com.pns.albang.repository.LandmarkRepository
import com.pns.albang.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class MainViewModel : ViewModel() {

    private val _landmarks = MutableLiveData<List<Landmark>>()
    private val _landmarkList = mutableListOf<Landmark>()
    val landmarks: LiveData<List<Landmark>> = _landmarks

    private val _targetLandmark = MutableLiveData<Event<Landmark?>>()
    val targetLandmark: LiveData<Event<Landmark?>> = _targetLandmark

    private val _showDialog = MutableLiveData<Event<String>>()
    val showDialog: LiveData<Event<String>> = _showDialog

    // BottomSheet
    private val _latLng = MutableLiveData<LatLng>()
    val latLng: LiveData<LatLng> = _latLng

    init {
        loadLandmarks()
    }

    private fun loadLandmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                LandmarkRepository.getLandmark().let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            Log.d(TAG, body.toString())

                            body.results.forEach { res ->
                                if (!_landmarkList.any { it.id == res.id }) {
                                    _landmarkList.add(
                                        Landmark(
                                            res.id,
                                            res.name,
                                            res.imageName,
                                            LatLng(res.latitude.toDouble(), res.longitude.toDouble()),
                                            Marker().apply {
                                                position = LatLng(res.latitude.toDouble(), res.longitude.toDouble())
                                                icon = OverlayImage.fromResource(R.drawable.ic_logo)
                                                tag = res.name
                                            }
                                        )
                                    )
                                }
                            }

                            _landmarks.postValue(_landmarkList)
                        }
                    } else {
                        Log.d(TAG, response.message())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun checkInBoundLandmark(bound: LatLngBounds) {
        val search = _landmarkList.filter { bound.contains(it.coordinate) }.run {
            if (this.isEmpty()) {
                null
            } else {
                this[0]
            }
        }

        _targetLandmark.postValue(Event(search))
    }

    fun showDialog(type: String) {
        _showDialog.postValue(Event(type))
    }

    fun setLocationForApplication(latLng: LatLng) {
        _latLng.value = latLng
    }

    fun applyLandmark(file: File, landmarkName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = JSONObject()
            json.put("name", landmarkName)
            json.put("latitude", latLng.value!!.latitude.toString())
            json.put("longitude", latLng.value!!.longitude.toString())

            val imageBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
            val imageMultipartBody = MultipartBody.Part.createFormData("image", file.name, imageBody)
            val jsonBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

            try {
                LandmarkRepository.applyLandmark(imageMultipartBody, jsonBody).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d(TAG, "$it")
                            showDialog("apply success")
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "MAIN VIEW MODEL"

    }
}
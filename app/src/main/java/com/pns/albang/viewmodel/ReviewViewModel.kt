package com.pns.albang.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pns.albang.AlBangApplication
import com.pns.albang.data.Review
import com.pns.albang.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class ReviewViewModel : ViewModel() {
    private val _reviews = mutableListOf<Review>()
    private val _reviewLiveData = MutableLiveData<List<Review>>()

    val reviewLiveData: LiveData<List<Review>> = _reviewLiveData

    fun getReviews(landmarkId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()
                ReviewRepository.getReviews(landmarkId).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            Log.d(TAG, body.toString())

                            _reviews.addAll(body.results.map {
                                Review(
                                    it.guestbookId,
                                    it.content,
                                    it.anchor,
                                    it.userId == userId,
                                    false
                                )
                            })
                            _reviewLiveData.postValue(_reviews)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.message.toString())
            }
        }
    }
    companion object {
        private const val TAG = "MY REVIEW VIEWMODEL"

        private const val USER_ID_KEY = "userId"
    }
}
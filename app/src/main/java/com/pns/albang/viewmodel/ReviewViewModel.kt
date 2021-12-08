package com.pns.albang.viewmodel

import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pns.albang.AlBangApplication
import com.pns.albang.data.Review
import com.pns.albang.remote.dto.guestbook.VanRequest
import com.pns.albang.repository.ReviewRepository
import com.pns.albang.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class ReviewViewModel : ViewModel() {
    private val _reviews = mutableListOf<Review>()
    private val _reviewLiveData = MutableLiveData<List<Review>>()

    val reviewLiveData: LiveData<List<Review>> = _reviewLiveData

    private val _showDialog = MutableLiveData<Event<String>>()

    val showDialog: LiveData<Event<String>> = _showDialog

    fun getReviews(landmarkId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()
                ReviewRepository.getReviews(landmarkId).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            Log.d(TAG, body.toString())
                            _reviews.clear()
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

    fun deleteReview(review: Review, dialogInterface: DialogInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ReviewRepository.deleteReview(review.reviewId).let { response ->
                    if (response.isSuccessful) {
                        _reviews.remove(review)
                        _reviewLiveData.postValue(_reviews)
                        dialogInterface.dismiss()
                        _showDialog.postValue(Event("delete success"))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun vanReview(review: Review, dialogInterface: DialogInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()
                ReviewRepository.requestVan(VanRequest(userId, review.reviewId)).let { response ->
                    if (response.isSuccessful) {
                        _reviews.remove(review)
                        _reviewLiveData.postValue(_reviews)
                        dialogInterface.dismiss()
                        _showDialog.postValue(Event("van success"))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getReviewArray(): ArrayList<Review> = if (_reviews.size < 10) {
        ArrayList(_reviews)
    } else {
        ArrayList(_reviews.subList(0, 10))
    }

    companion object {
        private const val TAG = "MY REVIEW VIEWMODEL"

        private const val USER_ID_KEY = "userId"
    }
}
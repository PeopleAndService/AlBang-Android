package com.pns.albang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pns.albang.AlBangApplication
import com.pns.albang.remote.dto.guestbook.ReviewRequest
import com.pns.albang.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class ReviewARViewModel : ViewModel() {

    fun setReview(content: String, anchor: String, landmarkId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()
                ReviewRepository.setReview(ReviewRequest(content, anchor, userId, landmarkId)).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            Log.d(TAG, body.toString())
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
        private const val TAG = "Review AR ViewModel"
        private const val USER_ID_KEY = "userId"
    }
}
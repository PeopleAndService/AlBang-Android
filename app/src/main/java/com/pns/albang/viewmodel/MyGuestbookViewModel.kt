package com.pns.albang.viewmodel

import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pns.albang.AlBangApplication
import com.pns.albang.data.MyGuestbook
import com.pns.albang.repository.MyGuestbookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MyGuestbookViewModel : ViewModel() {

    private val _myGuestbooks = mutableListOf<MyGuestbook>()
    private val _myGuestbooksAsLiveData = MutableLiveData<List<MyGuestbook>>()

    val myGuestbooksAsLiveData: LiveData<List<MyGuestbook>> = _myGuestbooksAsLiveData

    init {
        getMyGuestbook()
    }

    private fun getMyGuestbook() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()
                MyGuestbookRepository.getMyGuestbook(userId).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            Log.d(TAG, body.toString())

                            _myGuestbooks.addAll(body.results.map {
                                MyGuestbook(
                                    it.guestbookId,
                                    it.content,
                                    it.state,
                                    dateTimeConverter(it.createdTime),
                                    dateTimeConverter(it.updatedTime),
                                    it.landmarkName
                                )
                            })

                            _myGuestbooksAsLiveData.postValue(_myGuestbooks)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun deleteGuestbook(guestbook: MyGuestbook, dialog: DialogInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MyGuestbookRepository.deleteMyGuestbook(guestbook.guestbookId).let { response ->
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _myGuestbooks.remove(guestbook)

                            Log.d(TAG, "$_myGuestbooks")
                            _myGuestbooksAsLiveData.postValue(_myGuestbooks)

                            dialog.dismiss()
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
        private const val TAG = "MY GUESTBOOK VIEWMODEL"

        private const val USER_ID_KEY = "userId"

        private fun dateTimeConverter(dateTimeStr: String): String {
            val formatString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault())
            val dateTime =  formatString.parse(dateTimeStr)
            val convertString = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())

            return convertString.format(dateTime!!)
        }
    }
}
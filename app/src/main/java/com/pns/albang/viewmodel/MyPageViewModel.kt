package com.pns.albang.viewmodel

import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pns.albang.AlBangApplication
import com.pns.albang.remote.dto.user.UpdateNicknameRequest
import com.pns.albang.repository.UserRepository
import com.pns.albang.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class MyPageViewModel : ViewModel() {

    private val _showDialog = MutableLiveData<Event<String>>()
    private val _eventTrigger = MutableLiveData<Event<String>>()
    private val _validateNicknameResult = MutableLiveData<Event<String>>()

    val showDialog: LiveData<Event<String>> = _showDialog
    val eventTrigger: LiveData<Event<String>> = _eventTrigger
    val validateNicknameResult: LiveData<Event<String>> = _validateNicknameResult

    private val _userId = MutableLiveData<Long>()
    private val _userNickname = MutableLiveData<String>()
    private val _userEmail = MutableLiveData<String>()

    val userNickname: LiveData<String> = _userNickname
    val userEmail: LiveData<String> = _userEmail

    init {
        viewModelScope.launch {
            _userId.postValue(AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first())
            _userNickname.postValue(AlBangApplication.getApplication().getDataStore().getStringValue(USER_NICKNAME_KEY).first())
            _userEmail.postValue(AlBangApplication.getApplication().getDataStore().getStringValue(USER_EMAIL_KEY).first())
        }
    }

    fun showDialog(type: String) {
        _showDialog.value = Event(type)
    }

    fun validateNickname(inputNickname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.validateNickname(inputNickname).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (!body.isDuplicated) {
                                _validateNicknameResult.postValue(Event("available"))
                            } else {
                                _validateNicknameResult.postValue(Event("duplicated"))
                            }
                        }
                    } else {
                        _validateNicknameResult.postValue(Event("failed"))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.message ?: "")
                _validateNicknameResult.postValue(Event("failed"))
            }
        }
    }

    fun updateNickname(inputNickname: String, dialog: DialogInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()

                val request = if (inputNickname == "") {
                    UpdateNicknameRequest(
                        AlBangApplication.getApplication().getDataStore().getStringValue(USER_NAME_KEY).first()
                    )
                } else {
                    UpdateNicknameRequest(inputNickname)
                }

                UserRepository.updateNickname(userId, request).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d(TAG, it.toString())
                            val user = it.toUserModel()

                            _userNickname.postValue(user.nickname ?: "")
                            AlBangApplication.getApplication().getDataStore().setValue(USER_NICKNAME_KEY, user.nickname ?: "")

                            dialog.dismiss()
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

    fun signOut() {
        viewModelScope.launch {
            AlBangApplication.getApplication().getDataStore().removeLongKey(USER_ID_KEY)
            AlBangApplication.getApplication().getDataStore().removeStringKey(USER_EMAIL_KEY)
            AlBangApplication.getApplication().getDataStore().removeStringKey(USER_NICKNAME_KEY)
            AlBangApplication.getApplication().getDataStore().removeStringKey(USER_NAME_KEY)
            _eventTrigger.postValue(Event("sign out done"))
        }
    }

    fun withDraw() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _userId.value?.let {
                    UserRepository.withdraw(it).let { response ->
                        if (response.isSuccessful) {
                            AlBangApplication.getApplication().getDataStore().clear()

                            _eventTrigger.postValue(Event("withdraw done"))
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
        private const val TAG = "MYPAGE VIEW MODEL"

        private const val USER_ID_KEY = "userId"
        private const val USER_NICKNAME_KEY = "userNickname"
        private const val USER_EMAIL_KEY = "userEmail"
        private const val USER_NAME_KEY = "userName"
    }
}
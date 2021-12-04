package com.pns.albang.viewmodel

import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pns.albang.AlBangApplication
import com.pns.albang.data.User
import com.pns.albang.remote.dto.user.UpdateNicknameRequest
import com.pns.albang.repository.UserRepository
import com.pns.albang.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class LaunchViewModel : ViewModel() {

    private val _eventTrigger = MutableLiveData<Event<String>>()
    private val _showDialog = MutableLiveData<Event<String>>()
    private val _loginUser = MutableLiveData<User>()
    private val _validateNicknameResult = MutableLiveData<Event<String>>()

    val eventTrigger: LiveData<Event<String>> = _eventTrigger
    val showDialog: LiveData<Event<String>> = _showDialog
    val loginUser: LiveData<User> = _loginUser
    val validateNicknameResult: LiveData<Event<String>> = _validateNicknameResult

    init {
        setEvent("permission")
    }

    fun showDialog(type: String) {
        _showDialog.postValue(Event(type))
    }

    fun setEvent(event: String) {
        _eventTrigger.postValue(Event(event))
    }

    fun checkLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = AlBangApplication.getApplication().getDataStore().getLongValue(USER_ID_KEY).first()

            if (userId == 0L) {
                // 로그인 액티비티로
                setEvent("login fail")
            } else {
                try {
                    UserRepository.checkLogin(userId).let { response ->
                        if (response.isSuccessful) {
                            response.body()?.let {
                                Log.d(TAG, it.toString())
                                val user = it.toUserModel()

                                AlBangApplication.getApplication().getDataStore().setValue(USER_ID_KEY, user.userId)
                                AlBangApplication.getApplication().getDataStore().setValue(USER_EMAIL_KEY, user.email)
                                AlBangApplication.getApplication().getDataStore().setValue(USER_NAME_KEY, user.name)
                                AlBangApplication.getApplication().getDataStore().setValue(USER_NICKNAME_KEY, user.nickname ?: "")

                                _loginUser.postValue(it.toUserModel())
                            }
                        } else {
                            // 로그인 액티비티로
                            Log.d(TAG, response.message())
                            setEvent("login fail")
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, e.message ?: "")
                    // 로그인 액티비티로
                    setEvent("login fail")
                }
            }
        }
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

                UserRepository.updateNickname(userId, UpdateNicknameRequest(inputNickname)).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d(TAG, it.toString())
                            val user = it.toUserModel()

                            AlBangApplication.getApplication().getDataStore().setValue(USER_ID_KEY, user.userId)
                            AlBangApplication.getApplication().getDataStore().setValue(USER_EMAIL_KEY, user.email)
                            AlBangApplication.getApplication().getDataStore().setValue(USER_NAME_KEY, user.name)
                            AlBangApplication.getApplication().getDataStore().setValue(USER_NICKNAME_KEY, user.nickname ?: "")

                            _loginUser.postValue(it.toUserModel())

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

    companion object {
        private const val TAG = "LAUNCH VIEW MODEL"

        private const val USER_ID_KEY = "userId"
        private const val USER_EMAIL_KEY = "userEmail"
        private const val USER_NAME_KEY = "userName"
        private const val USER_NICKNAME_KEY = "userNickname"
    }
}
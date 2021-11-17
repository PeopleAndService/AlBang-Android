package com.pns.albang.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pns.albang.data.User
import com.pns.albang.util.Event

class LaunchViewModel : ViewModel() {

    private val _eventTrigger = MutableLiveData<Event<String>>()
    private val _showDialog = MutableLiveData<Event<String>>()
    private val _loginUser = MutableLiveData<User>()

    val eventTrigger: LiveData<Event<String>> = _eventTrigger
    val showDialog: LiveData<Event<String>> = _showDialog
    val loginUser: LiveData<User> = _loginUser

    private val testUser1 = User(1, "test1", null)
    private val testUser2 = User(2, "test2", "test2")

    init {
        setEvent("permission")
    }

    fun showDialog(type: String) {
        _showDialog.postValue(Event(type))
    }

    fun setEvent(event: String) {
        _eventTrigger.postValue(Event(event))
    }

    fun autoLogin(googleId: String) {
        when (googleId) {
            "test1" -> _loginUser.postValue(testUser1)
            "test2" -> _loginUser.postValue(testUser2)
        }
    }
}
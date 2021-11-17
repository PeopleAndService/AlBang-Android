package com.pns.albang.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pns.albang.util.Event

class LaunchViewModel : ViewModel() {

    private val _eventTrigger = MutableLiveData<Event<String>>()
    private val _showDialog = MutableLiveData<Event<String>>()

    val eventTrigger: LiveData<Event<String>> = _eventTrigger
    val showDialog: LiveData<Event<String>> = _showDialog

    init {
        _eventTrigger.postValue(Event("permission"))
    }

    fun showDialog(event: String) {
        _showDialog.postValue(Event(event))
    }
}
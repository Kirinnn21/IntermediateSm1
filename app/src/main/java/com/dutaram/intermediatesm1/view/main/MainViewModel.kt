package com.dutaram.intermediatesm1.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dutaram.intermediatesm1.data.pref.UserModel
import com.dutaram.intermediatesm1.data.pref.UserPreference

class MainViewModel(private val pref: UserPreference) : ViewModel() {


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}

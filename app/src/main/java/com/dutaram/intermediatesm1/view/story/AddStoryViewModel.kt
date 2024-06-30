package com.dutaram.intermediatesm1.view.story

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.dutaram.intermediatesm1.api.ApiClient.getApiService
import com.dutaram.intermediatesm1.api.response.BasicResponse
import com.dutaram.intermediatesm1.data.pref.UserModel
import com.dutaram.intermediatesm1.data.pref.UserPreference
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: UserPreference, private val context: Context) : ViewModel() {

    private val _finishActivity = MutableLiveData<Boolean>()
    val finishActivity: LiveData<Boolean> = _finishActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _location = MutableLiveData<android.location.Location>()
    val location: LiveData<android.location.Location> = _location

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        initializeLocationClient()
    }

    private fun initializeLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun getLocation() {
        _isLoading.value = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: android.location.Location? ->
                _isLoading.value = false
                location?.let {
                    _location.value = it
                } ?: run {
                    showResultDialog("Location Error", "Unable to retrieve current location", "Okay")
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                showResultDialog("Location Error", e.message, "Okay")
            }
    }

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, description: String, lat: Float? = null, lon: Float? = null) {
        _isLoading.value = true
        val client = if (lat != null && lon != null)
            getApiService().uploadStoryWithLocation(token, imageMultipart, description, lat, lon)
        else getApiService().uploadStory(token, imageMultipart, description)
        client.enqueue(object : Callback<BasicResponse> {

            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        showResultDialog("Upload Success!", "Your story has been posted! Go to the main page to view your story.", "Let's go!")
                    }
                } else {
                    showResultDialog("Upload Failed", response.message(), "Okay")
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                _isLoading.value = false
                showResultDialog("Upload Failed", t.message, "Okay")
            }
        })
    }

    fun uploadStoryWithLocation(token: String, imageMultipart: MultipartBody.Part, description: String, lat: Float, lon: Float) {
        uploadStory(token, imageMultipart, description, lat, lon)
    }


    private fun showResultDialog(title: String, message: String?, posButton: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(posButton) { _, _ ->
                if (posButton != "Okay") {
                    _finishActivity.value = true
                }
            }
            setCancelable(false)
            create()
            show()
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}

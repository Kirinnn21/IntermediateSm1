package com.dutaram.intermediatesm1.view.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dutaram.intermediatesm1.api.ApiClient
import com.dutaram.intermediatesm1.api.response.ListStoriesResponse
import com.dutaram.intermediatesm1.api.response.ListStoryItem
import com.dutaram.intermediatesm1.data.pref.UserModel
import com.dutaram.intermediatesm1.data.pref.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel(private val pref: UserPreference) : ViewModel() {

    private val _location = MutableLiveData<List<ListStoryItem>>()
    val location: LiveData<List<ListStoryItem>> = _location


    fun getAllStoriesWithLocation(token: String) {
        val client = ApiClient.getApiService().getAllStoriesWithLocation(token, 1)
        client.enqueue(object : Callback<ListStoriesResponse> {
            override fun onResponse(
                call: Call<ListStoriesResponse>,
                response: Response<ListStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val listStory = responseBody.listStory
                        val validatedList = listStory.filter { story ->
                            story.lat != null && story.lon != null &&
                                    GeoLocation.coordinatesValid(story.lat.toDouble(), story.lon.toDouble())
                        }
                        _location.value = validatedList
                    }
                } else {
                    onFailureLog("Response not successful: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                onFailureLog("Call failed: ${t.message}")
            }
        })
    }


    private fun onFailureLog(message: String) {
        Log.e(TAG, "onFailure: $message")
    }


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    companion object {
        private const val TAG = "MapViewModel"
    }
}

object GeoLocation {
    fun coordinatesValid(latitude: Double, longitude: Double): Boolean {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180
    }
}

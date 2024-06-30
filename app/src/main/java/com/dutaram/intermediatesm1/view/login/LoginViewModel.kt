package com.dutaram.intermediatesm1.view.login

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dutaram.intermediatesm1.api.ApiClient.getApiService
import com.dutaram.intermediatesm1.api.response.LoginResponse
import com.dutaram.intermediatesm1.data.pref.UserLoginModel
import com.dutaram.intermediatesm1.data.pref.UserModel
import com.dutaram.intermediatesm1.data.pref.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel(){

    private val _finishActivity = MutableLiveData<Boolean>()
    val finishActivity: LiveData<Boolean> = _finishActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loginUser(user: UserLoginModel, context: Context) {
        _isLoading.value = true
        val client = getApiService().loginUser(user)
        client.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        responseBody.loginResult?.let { login(it.token) }
                        showResultDialog("Login Success!", "Login successful! You're now ready to explore stories from others and share your own. Enjoy using this app!", "Let\'s go!", context)
                    }
                } else {
                    showResultDialog("Login Failed", response.message(), "Okay", context)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                showResultDialog("Login Failed", t.message, "Okay", context)
            }
        })
    }

    private fun showResultDialog(title: String, message: String?, posButton: String, context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(posButton) { _, _ ->
                if (posButton != "Okay"){
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

    fun login(token: String) {
        viewModelScope.launch {
            pref.login(token)
        }
    }
}
package com.dutaram.intermediatesm1.view.liststory


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dutaram.intermediatesm1.api.response.ListStoryItem
import com.dutaram.intermediatesm1.data.StoryRepository
import com.dutaram.intermediatesm1.data.pref.UserModel
import com.dutaram.intermediatesm1.data.pref.UserPreference
import kotlinx.coroutines.launch

class ListStoryViewModel(private val pref: UserPreference?, private val storyRepository: StoryRepository) : ViewModel(){


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setToken(token: String){
        storyRepository.setToken(token)
    }

    fun getAllStories(): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel>? {
        return pref?.getUser()?.asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref?.logout()
        }
    }
}
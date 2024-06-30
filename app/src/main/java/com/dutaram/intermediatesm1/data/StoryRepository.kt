package com.dutaram.intermediatesm1.data

import ApiService
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dutaram.intermediatesm1.api.response.ListStoryItem
import com.dutaram.intermediatesm1.data.local.StoryPagingSource

class StoryRepository(private val apiService: ApiService) {

    private var token = ""

    fun setToken(token: String) {
        this.token = token
    }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }
}
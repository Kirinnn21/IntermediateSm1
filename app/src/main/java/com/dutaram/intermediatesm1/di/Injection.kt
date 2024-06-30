package com.dutaram.intermediatesm1.di

import android.content.Context
import com.dutaram.intermediatesm1.api.ApiClient
import com.dutaram.intermediatesm1.data.StoryRepository


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiClient.getApiService()
        return StoryRepository(apiService)
    }
}
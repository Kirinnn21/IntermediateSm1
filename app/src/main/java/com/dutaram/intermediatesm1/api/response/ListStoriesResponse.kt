package com.dutaram.intermediatesm1.api.response

import com.google.gson.annotations.SerializedName

data class ListStoriesResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

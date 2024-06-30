package com.dutaram.intermediatesm1.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.dutaram.intermediatesm1.api.response.ListStoryItem

@Dao
interface StoryDAO {

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStoryItem>
}
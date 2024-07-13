package com.example.storyappsub2.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyappsub2.UserPreference
import com.example.storyappsub2.api.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StoryRepository(
    private val apiService: ApiService,
    private val userPrefs: UserPreference
) {

    fun fetchStories(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, "Bearer $token") }
        ).flow
    }

    fun fetchStoriesWithLocation(): LiveData<List<Story>> {
        val storiesLiveData = MutableLiveData<List<Story>>()
        CoroutineScope(Dispatchers.IO).launch {
            val token = userPrefs.token.firstOrNull()
            token?.let {
                try {
                    val response = apiService.getStoriesWithLocation("Bearer $it", 1)
                    storiesLiveData.postValue(response.listStory)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
        return storiesLiveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPrefs: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPrefs).also { instance = it }
            }
    }
}

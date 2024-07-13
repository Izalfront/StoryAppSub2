package com.example.storyappsub2.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyappsub2.story.Story
import com.example.storyappsub2.story.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storiesLiveData = MutableLiveData<List<Story>>()
    val storiesLiveData: LiveData<List<Story>> = _storiesLiveData

    init {
        loadStoriesWithLocation()
    }

    private fun loadStoriesWithLocation() {
        repository.fetchStoriesWithLocation().observeForever { storyList ->
            _storiesLiveData.postValue(storyList)
        }
    }
}

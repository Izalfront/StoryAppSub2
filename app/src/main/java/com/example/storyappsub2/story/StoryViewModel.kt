package com.example.storyappsub2.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

class StoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    lateinit var storyFlow: Flow<PagingData<Story>>

    fun initialize(token: String) {
        storyFlow = repository.fetchStories(token).cachedIn(viewModelScope)
    }
}

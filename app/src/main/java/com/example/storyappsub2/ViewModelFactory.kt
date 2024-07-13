package com.example.storyappsub2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsub2.api.ApiService
import com.example.storyappsub2.maps.MapsViewModel
import com.example.storyappsub2.story.StoryRepository

class ViewModelFactory(
    private val prefs: UserPreference,
    private val service: ApiService
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                val repository = StoryRepository.getInstance(service, prefs)
                MapsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(prefs: UserPreference, service: ApiService): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(prefs, service).also { instance = it }
            }
    }
}

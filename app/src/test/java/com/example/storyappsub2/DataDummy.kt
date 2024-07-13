package com.example.storyappsub2.test

import com.example.storyappsub2.story.Story

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val stories = mutableListOf<Story>()
        for (i in 0..10) {
            val story = Story(
                id = i.toString(),
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2022-12-12T12:12:12Z",
                lat = null,
                lon = null
            )
            stories.add(story)
        }
        return stories
    }
}

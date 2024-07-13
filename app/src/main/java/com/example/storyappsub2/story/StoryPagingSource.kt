package com.example.storyappsub2.story

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyappsub2.api.ApiService

class StoryPagingSource(
    private val apiService: ApiService,
    private val authToken: String
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val currentPage = params.key ?: INITIAL_PAGE_INDEX
        return try {
            val response = apiService.getStories(authToken, currentPage, params.loadSize)
            val stories = response.listStory

            LoadResult.Page(
                data = stories,
                prevKey = if (currentPage == INITIAL_PAGE_INDEX) null else currentPage - 1,
                nextKey = if (stories.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchor ->
            val closestPage = state.closestPageToPosition(anchor)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}

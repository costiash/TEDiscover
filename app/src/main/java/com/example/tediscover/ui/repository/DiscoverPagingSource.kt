package com.example.tediscover.ui.repository

import androidx.paging.PagingSource
import coil.network.HttpException
import com.example.tediscover.ui.repository.TedTalkRepository.Companion.DEFAULT_PAGE_INDEX
import com.example.tediscover.ui.models.EventItem
import java.io.IOException

class DiscoverPagingSource(
    private val tedApiService: Repo, private val urlQueries: String
    ): PagingSource<Int, EventItem>() {


    override suspend fun load(params: LoadParams<Int>) : LoadResult<Int, EventItem> {
        val page = params.key ?: DEFAULT_PAGE_INDEX
        return try {
            val response = tedApiService.searchEvents(page, params.loadSize, urlQueries)
            LoadResult.Page(
                response, prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
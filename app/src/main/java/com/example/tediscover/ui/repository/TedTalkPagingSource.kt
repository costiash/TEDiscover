package com.example.tediscover.ui.repository

import androidx.paging.PagingSource
import coil.network.HttpException
import com.example.tediscover.ui.models.EventItem
import java.io.IOException


class TedTalkPagingSource(private val tedApiService: Repo): PagingSource<Int, EventItem>() {


    override suspend fun load(params: LoadParams<Int>) : LoadResult<Int, EventItem>{
        val page = params.key ?: 1
        return try {
            val response = tedApiService.getEvents(page, params.loadSize)
            LoadResult.Page(
                response, prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
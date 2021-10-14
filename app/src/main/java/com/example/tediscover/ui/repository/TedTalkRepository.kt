package com.example.tediscover.ui.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.tediscover.ui.models.EventDetail
import com.example.tediscover.ui.models.EventItem
import kotlinx.coroutines.flow.Flow


class TedTalkRepository(private val tedApiService: Repo = Repo.getInstance()) {


    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 36

        //get tedTalk repository instance
        fun getInstance() = TedTalkRepository()
    }


    fun letTedTalksFlow(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<EventItem>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { TedTalkPagingSource(tedApiService) }
        ).flow
    }


    fun letDiscoverFlow(urlQueries: String, pagingConfig: PagingConfig = getDefaultPageConfig())
    : Flow<PagingData<EventItem>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { DiscoverPagingSource(tedApiService, urlQueries) }
        ).flow
    }

    /**
     * define page size, page size is the only required param, rest is optional
     */
    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }


    fun getEvent(url: String): EventDetail {
        return tedApiService.getEvent(url)
    }
}
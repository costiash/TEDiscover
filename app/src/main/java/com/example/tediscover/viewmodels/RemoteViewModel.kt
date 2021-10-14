package com.example.tediscover.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.tediscover.ui.models.EventDetail
import com.example.tediscover.ui.models.EventItem
import com.example.tediscover.ui.repository.TedTalkRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class RemoteViewModel(
    private val repository: TedTalkRepository = TedTalkRepository.getInstance()
) : ViewModel() {


    fun fetchTalks(): Flow<PagingData<EventItem>> {
        return repository.letTedTalksFlow()
    }


    fun fetchDiscoverTalks(urlQueries: String): Flow<PagingData<EventItem>> {
        return repository.letDiscoverFlow(urlQueries)
    }


    fun fetchEvent(url: String): MutableLiveData<EventDetail> {
        val item = MutableLiveData<EventDetail>()
        viewModelScope.launch(IO) {
            item.postValue(repository.getEvent(url))
        }
        return item
    }
}
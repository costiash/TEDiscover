package com.example.tediscover.ui.repository

import com.example.tediscover.ui.models.EventDetail
import com.example.tediscover.ui.models.EventItem
import org.jsoup.Jsoup
import java.io.IOException


class Repo {


    companion object {
        private var instance: Repo? = null
        fun getInstance(): Repo {
            if (instance == null)
                instance = Repo()

            return instance!!
        }
    }


    fun getEvents(page: Int, limit: Int): List<EventItem> {
        val url = "https://www.ted.com/talks?page=$page"
        return getEventsList(url)
    }


    fun searchEvents(page: Int, limit: Int, urlQueries: String): List<EventItem> {
        val url = "https://www.ted.com/talks?page=$page&$urlQueries"
        return getEventsList(url)
    }

    // Here is where the scraping methods, and additional methods related to cleaning some of the data, goes.

}

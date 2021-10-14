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


    private fun getEventsList(url: String): MutableList<EventItem> {
        val listData: MutableList<EventItem> = mutableListOf<EventItem>()
        try {
            val doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36")
                .get()
            val events = doc.select("#browse-results > div.row.row-sm-4up.row-lg-6up.row-skinny > div")
            var i = 0
            for (event in events) {
                val speaker = event.select("div.media__message > h4.h12.talk-link__speaker")
                    .text()
                val title = event.select("div.media__message > h4 > a")
                    .text()
                val date = event.select("div.media__message > div > span > span")
                    .text()
                val duration = event.select("span.thumb__duration")
                    .text()
                val talkUrl = "https://www.ted.com" + event.select("div.media__message > h4 > a").attr("href")
                val img = event.select("span.thumb__sizer > span > img").attr("src")
//                val newEvent = EventItem(i, img, duration, speaker, title, talkUrl, date)
                listData.add(EventItem(i, img, duration, speaker, title, talkUrl, date))
//                Log.d("getEventsList", "$newEvent")
                i += 1
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listData
    }


    fun getEvent(url: String): EventDetail {
        val item = EventDetail()
        try {
            val document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36")
                .get()
            val description = document.select("meta[name=description]").attr("content")
            val videoUrl = document.select("html > body > div:nth-of-type(1) > div:nth-of-type(2) > div > div:nth-of-type(2) > div > link:nth-of-type(3)").attr("href")
            val tagString = document.select("meta[name=keywords]").attr("content")
            val list: List<String> = listOf(*tagString.split(", ").toTypedArray())
            val tags = list.filter { it != "TED" && it != "talks" }

            item.description = description
            item.videoUrl = videoUrl
            item.tags = tags as MutableList<String>
        } catch (e: IOException) {
            e.printStackTrace()
        }
        extractVideoPath(item)
        return item
    }


    private fun extractVideoPath(item: EventDetail) {
        try {
            val document = Jsoup.connect(item.videoUrl).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36")
                .get()
            val sel = document.select("html > body > script:nth-of-type(3)")
            val script = sel.toString()
            val urls = countMatches(script)
            val url = urls.filter{ it.contains("https://") }[0]
            item.videoUrl = url
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun countMatches(string: String): MutableList<String> {
        val pattern = "stream"
        var index = 0
        var count = 0
        val urls = mutableListOf<String>()

        while (true)
        {
            index = string.indexOf(pattern, index)
            urls.add(extractUrl(string, index))
            index += if (index != -1)
            {
                count++
                pattern.length
            }
            else {
                return urls
            }
        }
    }


    private fun extractUrl(script: String, stream: Int): String {
        val indexOfComma = script.indexOf(',', stream)
        return script.substring(stream + 9, indexOfComma - 1)
    }
}
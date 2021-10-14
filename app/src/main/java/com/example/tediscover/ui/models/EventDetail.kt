package com.example.tediscover.ui.models

data class EventDetail (
    var tags: MutableList<String> = mutableListOf(),
    var description: String = "",
    var videoUrl: String = ""
)
package com.example.tediscover.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TalkItem(
    var id: String = "",
    var thumbImage: String = "",
    var duration: String = "",
    var speaker: String = "",
    var title: String = "",
    var date: String = "",
    var tags: MutableList<String> = mutableListOf(),
    var description: String = "",
    var videoUrl: String = ""
) : Parcelable

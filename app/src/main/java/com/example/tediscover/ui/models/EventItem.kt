package com.example.tediscover.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class EventItem(
    var id: Int = 0,
    var thumbImage: String = "",
    var duration: String = "",
    var speaker: String = "",
    var title: String = "",
    var talkUrl: String = "",
    var date: String = ""
) : Parcelable
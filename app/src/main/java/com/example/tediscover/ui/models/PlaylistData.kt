package com.example.tediscover.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistData(
    var playlistName: String = "",
    var playlistItems: MutableList<TalkItem> = mutableListOf(),
    var videosNumber: Int = 0
) : Parcelable

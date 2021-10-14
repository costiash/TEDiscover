package com.example.tediscover.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchQuery(
    var duration: String = "",
    var topics: MutableList<String> = mutableListOf(),
    var sortBy: String = "",
    var userQuery: String = ""
) : Parcelable

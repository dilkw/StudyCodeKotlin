package com.dilkw.studycodekotlin.network

import com.google.gson.annotations.SerializedName

data class Reactions(
    @SerializedName("+1")
    val IncreaseOne: Int,
    @SerializedName("-1")
    val decreaseOne: Int,
    val confused: Int,
    val eyes: Int,
    val heart: Int,
    val hooray: Int,
    val laugh: Int,
    val rocket: Int,
    val total_count: Int,
    val url: String
)
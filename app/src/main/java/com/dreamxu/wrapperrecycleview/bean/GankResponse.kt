package com.dreamxu.wrapperrecycleview.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GankResponse(
    @SerializedName("data")
    val result: List<NewsEntity>
): Serializable

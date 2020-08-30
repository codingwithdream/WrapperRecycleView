package com.dreamxu.wrapperrecycleview.bean

import java.io.Serializable

data class NewsEntity(
    val desc: String,
    val views: Int,
    val publishedAt: String,
    val images: List<String>
): Serializable

package com.example.jeeek.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Tweet(
    val id: String = "",
    val content: Content? = null,
    val category: Int = 0,
    val userTiny: UserTiny? = null,
    val rank: Int = 0,
    val tags: ArrayList<String>? = null,
    val favorites: ArrayList<String>? = null,
    val updatedAt: Timestamp? = null
)

data class Content(
    val subject: String = "",
    val url: String = "",
    val comment: String = ""
)
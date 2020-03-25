package com.example.jeeek.domain

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Tweet(
    val id: String,
    val content: Content,
    val category: Int,
    val userTiny: UserTiny,
    val rank: Int,
    val tags: ArrayList<String>,
    val favorites: ArrayList<String>,
    val updatedAt: String
)

data class Content(
    val subject: String,
    val url: String,
    val comment: String
)
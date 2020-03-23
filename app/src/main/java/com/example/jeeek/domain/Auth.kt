package com.example.jeeek.domain

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class EmailPasswordPayload (
    val email: String,
    val password: String
)

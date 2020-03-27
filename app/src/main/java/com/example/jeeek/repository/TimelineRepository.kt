package com.example.jeeek.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


/**
 * Repository for fetching orders from the network and storing them on disk
 */
class TimelineRepository {

    private val firestoreDB = FirebaseFirestore.getInstance()

    fun getTimeline(uid: String): Query {
        return firestoreDB.collection("users")
            .document(uid)
            .collection("timeline")
            .orderBy("updatedAt")
    }

}
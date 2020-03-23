package com.example.jeeek.repository

import com.example.jeeek.domain.EmailPasswordPayload
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthRepository(private val auth: FirebaseAuth) {

    suspend fun signin(payload: EmailPasswordPayload) {
        Timber.d("get access token by auth code")

        // payloadのempty/nullチェックは外側
        withContext(Dispatchers.IO) {
            val response = auth.signInWithEmailAndPassword(
                payload.email,
                payload.password
            ).await()
            Timber.d(response.toString())
        }
    }

    suspend fun signup(payload: EmailPasswordPayload) {
        Timber.d("get access token by auth code")

        // payloadのempty/nullチェックは外側
        withContext(Dispatchers.IO) {
            val response = auth.createUserWithEmailAndPassword(
                payload.email,
                payload.password
            ).await()
            Timber.d(response.toString())
        }
    }

}

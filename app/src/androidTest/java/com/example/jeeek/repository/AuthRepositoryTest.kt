package com.example.jeeek.repository

import androidx.test.runner.AndroidJUnit4
import com.example.jeeek.domain.EmailPasswordPayload
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber


@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        auth = FirebaseAuth.getInstance()
        repository = AuthRepository(auth)
    }

    @Test
    fun signinOk() {
        val email = "aaaa@gmail.com"
        val password = "aaaaaa"

        runBlocking {
            repository.signin(EmailPasswordPayload(email, password))
        }

        assertNotNull(auth.currentUser)
        assertEquals(auth.currentUser!!.email, email)
        auth.signOut()
    }

    @Test
    fun signinFail() {
        val email = "aaaa@gmail.com"
        val password = "xxxxxx"

        runBlocking {
            try {
                repository.signin(EmailPasswordPayload(email, password))
            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }

        assertNull(auth.currentUser)
    }

    @Test
    fun signupTest() {
        val email = "cccc@gmail.com"
        val password = "cccccc"

        runBlocking {
            repository.signup(EmailPasswordPayload(email, password))
        }

        assertNotNull(auth.currentUser)
        assertEquals(auth.currentUser!!.email, email)

        auth.currentUser!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("User account deleted.")
                } else {
                    Timber.d("Failed: delete user account.")
                }
            }
    }

}
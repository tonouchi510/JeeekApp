package com.example.jeeek.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import com.example.jeeek.domain.EmailPasswordPayload
import com.example.jeeek.domain.Tweet
import com.example.jeeek.repository.AuthRepository
import com.example.jeeek.utils.getOrAwaitValue
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import timber.log.Timber

class TimelineViewModelTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: AuthRepository
    private lateinit var timelineViewModel: TimelineViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        auth = FirebaseAuth.getInstance()
        repository = AuthRepository(auth)
        val email = "tonouchi27@gmail.com"
        val password = "udon2307"

        runBlocking {
            repository.signin(EmailPasswordPayload(email, password))
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val firebaseApp = FirebaseApp.initializeApp(appContext)
        firebaseApp!!.setAutomaticResourceManagementEnabled(true)

        timelineViewModel = TimelineViewModel(Application())

    }

    @Test
    fun flagLiveDataTest() {

        val observer = Observer<Boolean> {}
        try {
            timelineViewModel.flagForTest.observeForever(observer)
            assertEquals(false, timelineViewModel.flagForTest.value)

            timelineViewModel.flagForTest.value = true
            assertEquals(true, timelineViewModel.flagForTest.value)
        } catch (e: Exception) {
            Timber.e(e.message)
        } finally {
            timelineViewModel.flagForTest.removeObserver(observer)
        }
    }

    @Test
    fun timelineTest() {

        val observer = Observer<List<Tweet>> {}
        try {
            timelineViewModel.timeline.observeForever(observer)
            timelineViewModel.getTimelineFromRepository()

            val value = timelineViewModel.timeline.getOrAwaitValue()
            assertNotNull(value)
        } catch (e: Exception) {
            Timber.e(e.message)
        } finally {
            timelineViewModel.timeline.removeObserver(observer)
        }
    }

}
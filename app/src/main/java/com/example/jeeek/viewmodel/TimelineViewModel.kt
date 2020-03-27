package com.example.jeeek.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.jeeek.domain.Tweet
import com.example.jeeek.repository.TimelineRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    private val timelineRepository = TimelineRepository()

    val timeline: MutableLiveData<List<Tweet>> = MutableLiveData()

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    fun getTimelineFromRepository(): LiveData<List<Tweet>> {

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        Timber.d(uid)

        viewModelScope.launch {
            timelineRepository.getTimeline(uid).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Timber.w("Listen failed:$e")
                    timeline.value = null
                    _eventNetworkError.value = true
                    return@EventListener
                }

                val savedTweets: MutableList<Tweet> = mutableListOf()
                for (doc in value!!) {
                    Timber.d(doc.data.toString())
                    val item = doc.toObject(Tweet::class.java)
                    savedTweets.add(item)
                }
                timeline.value = savedTweets
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            })
        }
        return timeline
    }


    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing TimelineViewModel with parameter
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
                return TimelineViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
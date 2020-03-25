package com.example.jeeek.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jeeek.R
import com.example.jeeek.adapter.TimelineRecyclerViewAdapter
import com.example.jeeek.databinding.FragmentTimelineBinding
import com.example.jeeek.domain.Tweet
import com.example.jeeek.viewmodel.TimelineViewModel

class FragmentTimeline: Fragment(){

    private val viewModel: TimelineViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, TimelineViewModel.Factory(activity.application))
            .get(TimelineViewModel::class.java)
    }

    private var recyclerViewAdapter: TimelineRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentTimelineBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_timeline, container, false)

        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        recyclerViewAdapter = TimelineRecyclerViewAdapter()

        binding.root.findViewById<RecyclerView>(R.id.order_recyclerview).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }

        // Observer for the network error.
        viewModel.eventNetworkError.observe(this, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getTimelineFromRepository().observe(viewLifecycleOwner, Observer<List<Tweet>> { tweets ->
            tweets?.apply {
                recyclerViewAdapter?.timeline = tweets
            }
        })
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}
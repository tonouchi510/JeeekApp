package com.example.jeeek.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.jeeek.R
import com.example.jeeek.databinding.ItemTweetBinding
import com.example.jeeek.domain.Tweet

class TimelineRecyclerViewAdapter : RecyclerView.Adapter<TimelineViewHolder>() {
    var timeline: List<Tweet> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val withDataBinding: ItemTweetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            TimelineViewHolder.LAYOUT,
            parent,
            false)
        return TimelineViewHolder(withDataBinding)
    }

    override fun getItemCount() = timeline.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.tweet = timeline[position]
        }
    }
}

/**
 * ViewHolder for Order items. All work is done by data binding.
 */
class TimelineViewHolder(val viewDataBinding: ItemTweetBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_tweet
    }
}
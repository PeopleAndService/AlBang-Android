package com.pns.albang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pns.albang.databinding.ItemReviewBinding

class ReviewAdapter(private val itemClick: (Review) -> Unit) : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.review = review
            binding.root.setOnClickListener { itemClick(review) }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Review>() {
            override fun areContentsTheSame(oldItem: Review, newItem: Review) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Review, newItem: Review) =
                oldItem.reviewId == newItem.reviewId
        }
    }
}
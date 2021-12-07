package com.pns.albang.util

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pns.albang.data.MyGuestbook
import com.pns.albang.data.Review
import com.pns.albang.view.adapter.MyGuestbookAdapter
import com.pns.albang.view.adapter.ReviewAdapter

object CustomBindingAdapter {

    @JvmStatic
    @BindingAdapter("myGuestbookItems")
    fun bindMyGuestbook(recyclerView: RecyclerView, items: List<MyGuestbook>?) {
        Log.d("bind rcview", "$items")
        val adapter = recyclerView.adapter as MyGuestbookAdapter
        adapter.submitList(items?.toMutableList())
    }

    @JvmStatic
    @BindingAdapter("reviewItems")
    fun bindReviews(recyclerView: RecyclerView, items: List<Review>?) {
        Log.d("bind review rcview", "$items")
        val adapter = recyclerView.adapter as ReviewAdapter
        adapter.submitList(items?.toMutableList())
    }
}
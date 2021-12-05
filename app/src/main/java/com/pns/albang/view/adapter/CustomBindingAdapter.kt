package com.pns.albang.view.adapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pns.albang.data.MyGuestbook

object CustomBindingAdapter {

    @JvmStatic
    @BindingAdapter("myGuestbookItems")
    fun bindMyGuestbook(recyclerView: RecyclerView, items: List<MyGuestbook>?) {
        Log.d("bind rcview", "$items")
        val adapter = recyclerView.adapter as MyGuestbookAdapter
        adapter.submitList(items?.toMutableList())
    }
}
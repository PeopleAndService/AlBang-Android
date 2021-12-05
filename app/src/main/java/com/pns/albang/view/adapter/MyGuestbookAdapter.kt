package com.pns.albang.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pns.albang.data.MyGuestbook
import com.pns.albang.databinding.ItemMyGuestbookBinding

class MyGuestbookAdapter(private val deleteGuestbook: (item: MyGuestbook) -> Unit) :
    ListAdapter<MyGuestbook, MyGuestbookAdapter.MyGuestbookViewHolder>(
        myGuestbookDiffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGuestbookViewHolder {
        val itemBinding = ItemMyGuestbookBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyGuestbookViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyGuestbookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyGuestbookViewHolder(private val itemBinding: ItemMyGuestbookBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: MyGuestbook) {
            itemBinding.data = item

            itemBinding.btnDelete.setOnClickListener {
                deleteGuestbook(getItem(adapterPosition))
            }
        }
    }

    companion object {
        val myGuestbookDiffUtil = object : DiffUtil.ItemCallback<MyGuestbook>() {
            override fun areItemsTheSame(oldItem: MyGuestbook, newItem: MyGuestbook): Boolean {
                return oldItem.guestbookId == newItem.guestbookId
            }

            override fun areContentsTheSame(oldItem: MyGuestbook, newItem: MyGuestbook): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }
}
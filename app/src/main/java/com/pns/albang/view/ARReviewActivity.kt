package com.pns.albang.view

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pns.albang.R
import com.pns.albang.databinding.ActivityArReviewBinding

class ARReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArReviewBinding
    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabReview.setOnClickListener {
            toggleFab()
        }

        binding.fabRefresh.setOnClickListener {

        }

        binding.fabList.setOnClickListener {
            finish()
        }

        binding.fabAdd.setOnClickListener {

        }
    }

    private fun toggleFab() {
        if (isFabOpen) {
            binding.fabBackground.visibility = View.INVISIBLE
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabList, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabRefresh, "translationY", 0f).apply { start() }
            binding.fabReview.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.colorSecondary))
            ObjectAnimator.ofFloat(binding.fabReview, View.ROTATION, 45f, 0f).apply { start() }
            binding.fabReview.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.fab_review_ar))
        } else {
            binding.fabBackground.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabList, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabRefresh, "translationY", -600f).apply { start() }
            binding.fabReview.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            binding.fabReview.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.fab_review_add))
            ObjectAnimator.ofFloat(binding.fabReview, View.ROTATION, 0f, 45f).apply { start() }
        }
        isFabOpen = !isFabOpen
    }
}
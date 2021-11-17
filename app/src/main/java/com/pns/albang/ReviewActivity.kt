package com.pns.albang

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pns.albang.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private var isFabOpen = false

    private var testList = mutableListOf(Review("1", "내가 쓴 방명록", true, false),
        Review("2", "신고하지 않은 방명록", false, false),
        Review("3", "신고완료 한 방명록", false, true))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.heritageName = "경복궁"

        binding.fabReview.setOnClickListener {
            toggleFab()
        }

        settingRecyclerview()
    }

    private fun settingRecyclerview() {
        reviewAdapter = ReviewAdapter()
        binding.rcReview.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = reviewAdapter
        }
        reviewAdapter.submitList(testList)

    }

    private fun toggleFab() {
        if (isFabOpen) {
            binding.fabBackground.visibility = View.INVISIBLE
            binding.motionLayout.isInteractionEnabled = true
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabAr, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabRefresh, "translationY", 0f).apply { start() }
            binding.fabReview.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.colorSecondary))
            ObjectAnimator.ofFloat(binding.fabReview, View.ROTATION, 45f, 0f).apply { start() }
            binding.fabReview.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.fab_review_ar))
        } else {
            binding.fabBackground.visibility = View.VISIBLE
            binding.motionLayout.isInteractionEnabled = false
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", -250f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabAr, "translationY", -500f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabRefresh, "translationY", -750f).apply { start() }
            binding.fabReview.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            binding.fabReview.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.fab_review_add))
            ObjectAnimator.ofFloat(binding.fabReview, View.ROTATION, 0f, 45f).apply { start() }
        }
        isFabOpen = !isFabOpen
    }
}
package com.pns.albang.view

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.albang.R
import com.pns.albang.data.Review
import com.pns.albang.view.adapter.ReviewAdapter
import com.pns.albang.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private var isFabOpen = false

    private var testList = mutableListOf(
        Review("1", "내가 쓴 방명록", true, false),
        Review("2", "신고하지 않은 방명록", false, false),
        Review("3", "신고완료 한 방명록", false, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.heritageName = intent.getStringExtra(LANDMARK_NAME)
        intent.getStringExtra(LANDMARK_IMAGE_NAME)?.let { setLandmarkImage(it) }


        binding.fabReview.setOnClickListener {
            toggleFab()
        }

        settingRecyclerview()
    }

    private fun settingRecyclerview() {
        reviewAdapter = ReviewAdapter { review -> reviewOnClick(review) }
        binding.rcReview.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = reviewAdapter
        }
        reviewAdapter.submitList(testList)

    }

    private fun reviewOnClick(review: Review){
        if (review.isMine) {
            createDeleteDialog(review)
        } else {
            if (!review.isReport) {
                createNotifyDialog(review)
            }
        }
    }

    private fun createDeleteDialog(review: Review) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.dialog_title_delete))
            .setMessage(resources.getString(R.string.dialog_message_review, review.reviewContent))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                // TODO : 삭제 로직
                dialog.dismiss()
                createFinishDeleteDialog()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createNotifyDialog(review: Review) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.dialog_title_notify))
            .setMessage(resources.getString(R.string.dialog_message_review, review.reviewContent))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                // TODO : 신고 로직
                dialog.dismiss()
                createFinishNotifyDialog()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createFinishNotifyDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.dialog_title_finish_notify))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createFinishDeleteDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.dialog_title_finish_delete))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    private fun setLandmarkImage(fileName: String) {
        Glide
            .with(binding.root)
            .load(FILE_URL+fileName)
            .fitCenter()
            .into(binding.ivHeritage)
    }

    companion object {
        private const val LANDMARK_ID = "landmarkID"
        private const val LANDMARK_IMAGE_NAME = "landmarkImage"
        private const val LANDMARK_NAME = "landmarkName"
        private const val FILE_URL = "http://203.255.3.231:1130/file/get/"

        fun newIntent(packageContext: Context, landmarkId: Long, landmarkImgName: String, landmarkName: String): Intent {
            return Intent(packageContext, ReviewActivity::class.java).apply {
                putExtra(LANDMARK_ID, landmarkId)
                putExtra(LANDMARK_IMAGE_NAME, landmarkImgName)
                putExtra(LANDMARK_NAME, landmarkName)
            }
        }
    }
}
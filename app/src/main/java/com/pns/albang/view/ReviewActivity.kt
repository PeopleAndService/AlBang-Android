package com.pns.albang.view

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.albang.R
import com.pns.albang.data.Review
import com.pns.albang.databinding.ActivityReviewBinding
import com.pns.albang.databinding.DialogReviewBinding
import com.pns.albang.view.adapter.ReviewAdapter
import com.pns.albang.viewmodel.ReviewViewModel

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewList: ArrayList<Review>
    private val viewModel: ReviewViewModel by viewModels()
    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        binding.heritageName = intent.getStringExtra(LANDMARK_NAME)
        intent.getStringExtra(LANDMARK_IMAGE_NAME)?.let { setLandmarkImage(it) }


        binding.fabReview.setOnClickListener {
            toggleFab()
        }

        binding.fabRefresh.setOnClickListener {
            viewModel.getReviews(intent.getLongExtra(LANDMARK_ID, 0L))
        }

        binding.fabAr.setOnClickListener {
            toggleFab()
            reviewList = viewModel.getReviewArray()
            Log.d(TAG, "$reviewList")
            startActivity(
                Intent(
                    ARReviewActivity.newIntent(
                        this,
                        "",
                        reviewList,
                        intent.getLongExtra(LANDMARK_ID, 0L)
                    )
                )
            )
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.fabAdd.setOnClickListener {
            toggleFab()
            createReviewAddDialog()
        }

        viewModel.getReviews(intent.getLongExtra(LANDMARK_ID, 0L))
        settingRecyclerview()

        viewModel.showDialog.observe(this) {
            it.getContentIfNotHandled()?.let { content ->
                when (content) {
                    "delete success" -> createFinishDeleteDialog()
                    "van success" -> createFinishNotifyDialog()
                }
            }
        }
    }

    private fun settingRecyclerview() {
        reviewAdapter = ReviewAdapter { review -> reviewOnClick(review) }
        binding.rcReview.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = reviewAdapter
        }
    }

    private fun reviewOnClick(review: Review) {
        if (review.isMine) {
            createDeleteDialog(review)
        } else {
            if (!review.isReport) {
                createNotifyDialog(review)
            }
        }
    }

    private fun createDeleteDialog(review: Review) = MaterialAlertDialogBuilder(this)
        .setTitle(resources.getString(R.string.dialog_title_delete))
        .setMessage(resources.getString(R.string.dialog_message_review, review.reviewContent))
        .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
            viewModel.deleteReview(review, dialog)
        }
        .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()

    private fun createNotifyDialog(review: Review) = MaterialAlertDialogBuilder(this)
        .setTitle(resources.getString(R.string.dialog_title_notify))
        .setMessage(resources.getString(R.string.dialog_message_review, review.reviewContent))
        .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
            viewModel.vanReview(review, dialog)
        }
        .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()

    private fun createFinishNotifyDialog() = MaterialAlertDialogBuilder(this)
        .setTitle(resources.getString(R.string.dialog_title_finish_notify))
        .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()

    private fun createFinishDeleteDialog() = MaterialAlertDialogBuilder(this)
        .setTitle(resources.getString(R.string.dialog_title_finish_delete))
        .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()

    private fun createReviewAddDialog() {
        val dialogReviewBinding = DialogReviewBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_review_add))
            .setView(dialogReviewBinding.root)
            .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                Log.d(TAG, dialogReviewBinding.etReview.text.toString())
                reviewList = viewModel.getReviewArray()
                Log.d(TAG, "$reviewList")
                startActivity(
                    ARReviewActivity.newIntent(
                        this,
                        dialogReviewBinding.etReview.text.toString(),
                        reviewList,
                        intent.getLongExtra(LANDMARK_ID, 0L)
                    )
                )
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
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
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabAr, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabRefresh, "translationY", -600f).apply { start() }
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
            .load(FILE_URL + fileName)
            .fitCenter()
            .into(binding.ivHeritage)
    }

    companion object {
        private const val TAG = "Review Activity"
        private const val LANDMARK_ID = "landmarkID"
        private const val LANDMARK_IMAGE_NAME = "landmarkImage"
        private const val LANDMARK_NAME = "landmarkName"
        private const val FILE_URL = "http://203.255.3.231:1130/file/get/"

        fun newIntent(
            packageContext: Context,
            landmarkId: Long,
            landmarkImgName: String,
            landmarkName: String
        ): Intent {
            return Intent(packageContext, ReviewActivity::class.java).apply {
                putExtra(LANDMARK_ID, landmarkId)
                putExtra(LANDMARK_IMAGE_NAME, landmarkImgName)
                putExtra(LANDMARK_NAME, landmarkName)
            }
        }
    }
}
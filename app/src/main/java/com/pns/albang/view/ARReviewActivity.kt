package com.pns.albang.view

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.pns.albang.R
import com.pns.albang.databinding.ActivityArReviewBinding
import com.pns.albang.databinding.ViewArReviewBinding

class ARReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArReviewBinding
    private lateinit var arFragment: CloudAnchorFragment

    private var isFabOpen = false
    private var isNewAdd = false
    private var isFirstTab = true
    private var cloudAnchor: Anchor? = null
    var appAnchorState = AppAnchorState.NONE

    enum class AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arSetting()

        binding.fabReview.setOnClickListener {
            binding.groupFab.bringToFront()
            toggleFab()
        }

        binding.fabRefresh.setOnClickListener {

        }

        binding.fabList.setOnClickListener {
            finish()
        }

        binding.fabAdd.setOnClickListener {

        }

        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING && !isNewAdd) {
                return@setOnTapArPlaneListener
            }
            if (isFirstTab) {
                isFirstTab = !isFirstTab
            } else {
                val anchor = arFragment.arSceneView.session?.hostCloudAnchor(hitResult.createAnchor())
                if (anchor != null) {
                    newAnchor(anchor, "testData")
                } else {
                    Log.e(TAG, "No anchor")
                }
            }
        }
    }

    private fun arSetting() {
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as CloudAnchorFragment
        arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdateFrame)

        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        checkUpdatedAnchor()
    }

    @Synchronized
    private fun checkUpdatedAnchor() {
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING)
            return
        val cloudState: Anchor.CloudAnchorState = cloudAnchor?.cloudAnchorState!!
        if (appAnchorState == AppAnchorState.HOSTING) {
            if (cloudState.isError) {
                Log.d(TAG, "Error hosting anchor ... ")
                appAnchorState = AppAnchorState.NONE
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                Log.d(TAG, "cloudAnchor?.cloudAnchorId : ${cloudAnchor?.cloudAnchorId}")
                appAnchorState = AppAnchorState.HOSTED
            }
        } else if (appAnchorState == AppAnchorState.RESOLVING) {
            if (cloudState.isError) {
                Log.d(TAG, "Error resolving anchor ...")
                appAnchorState = AppAnchorState.NONE
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                appAnchorState = AppAnchorState.RESOLVED
            }
        }
    }

    private fun newAnchor(anchor: Anchor, text: String) {
        cloudAnchor(anchor)
        appAnchorState = AppAnchorState.HOSTING
        placeReview(arFragment, cloudAnchor!!, text)
    }

    private fun resolveAnchor(cloudAnchorId: String, text: String) {
        val resolvedAnchor = arFragment.arSceneView.session?.resolveCloudAnchor(cloudAnchorId)
        Log.d(TAG, "$resolvedAnchor")
        if (resolvedAnchor != null) {
            placeReview(arFragment, resolvedAnchor, text)
        }
    }

    private fun cloudAnchor(newAnchor: Anchor?) {
        cloudAnchor?.detach()
        cloudAnchor = newAnchor
        appAnchorState = AppAnchorState.NONE
    }

    private fun placeReview(fragment: ArFragment, anchor: Anchor, review: String) {
        val itemBinding = ViewArReviewBinding.inflate(layoutInflater)
        itemBinding.tvArReview.text = review
        ViewRenderable.builder()
            .setView(this, itemBinding.root)
            .build()
            .thenAccept { renderable -> addNodeToScene(fragment, anchor, renderable) }
            .exceptionally {
                Log.d(TAG, "Unable to load view render")
                null
            }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ViewRenderable?) {
        val node = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderable
        transformableNode.setParent(node)
        fragment.arSceneView.scene.addChild(node)
        transformableNode.select()
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

    companion object {
        const val TAG = "AR Review Activity"
    }
}
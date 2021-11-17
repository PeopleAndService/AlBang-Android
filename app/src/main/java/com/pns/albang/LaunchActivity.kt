package com.pns.albang

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.pns.albang.viewmodel.LaunchViewModel

class LaunchActivity : AppCompatActivity() {

    private val viewModel: LaunchViewModel by viewModels()

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Log.d(TAG, "permission granted")
            navigateActivity("main")
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Log.d(TAG, deniedPermissions.toString())
            viewModel.showDialog("permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()
    }

    /*override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            navigateActivity("login")
        } else {
            navigateActivity("main")
        }
    }*/

    private fun setObserver() {
        viewModel.eventTrigger.observe(this, {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    "permission" -> {
                        checkPermission(permissionListener)
                    }
                }
            }
        })

        viewModel.showDialog.observe(this, {
            it.getContentIfNotHandled()?.let { type ->
                showAlertDialog(type)
            }
        })
    }

    private fun checkPermission(permissionListener: PermissionListener) =
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setRationaleTitle(getString(R.string.permission_rational_title))
            .setRationaleMessage(getString(R.string.permission_rational_message))
            .setRationaleConfirmText(getString(R.string.permission_rational_button))
            .setDeniedTitle(getString(R.string.permission_denied_title))
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setDeniedCloseButtonText(getString(R.string.btn_confirm))
            .setGotoSettingButton(false)
            .setPermissions(FINE_LOCATION, COARSE_LOCATION, CAMERA)
            .check()

    private fun navigateActivity(where: String) {
        when (where) {
            "main" -> {
                startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                finish()
            }
            "login" -> {
                startActivity(Intent(this@LaunchActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun showAlertDialog(type: String) {
        when (type) {
            "permission denied" -> {
                MaterialAlertDialogBuilder(this@LaunchActivity)
                    .setTitle(getString(R.string.permission_denied_dialog_message))
                    .setPositiveButton(getString(R.string.permission_denied_dialog_button)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    companion object {
        private const val TAG = "LAUNCH ACTIVITY"

        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val CAMERA = Manifest.permission.CAMERA
    }
}
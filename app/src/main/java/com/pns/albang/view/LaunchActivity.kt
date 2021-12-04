package com.pns.albang.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.pns.albang.R
import com.pns.albang.databinding.DialogNicknameBinding
import com.pns.albang.viewmodel.LaunchViewModel

class LaunchActivity : AppCompatActivity() {

    private val viewModel: LaunchViewModel by viewModels()

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Log.d(TAG, "permission granted")
            viewModel.setEvent("autoLogin")
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

    private fun setObserver() {
        viewModel.eventTrigger.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    "permission" -> {
                        checkPermission(permissionListener)
                    }
                    "autoLogin" -> {
                        getLastLoginAcc()
                    }
                    "login fail" -> {
                        navigateActivity("login")
                    }
                }
            }
        }

        viewModel.showDialog.observe(this) {
            it.getContentIfNotHandled()?.let { type ->
                showAlertDialog(type)
            }
        }

        viewModel.loginUser.observe(this) {
            if (it.nickname == null) {
                viewModel.showDialog("register nickname")
            } else {
                navigateActivity("main")
            }
        }

        viewModel.validateNicknameResult.observe(this) {
            it.getContentIfNotHandled()?.let { result ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.validate_nickname_title))
                    .setMessage(
                        when (result) {
                            "duplicated" -> getString(R.string.nickname_duplicated)
                            "available" -> getString(R.string.nickname_available)
                            else -> getString(R.string.validate_nickname_fail)
                        }
                    )
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
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

    private fun getLastLoginAcc() {
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            navigateActivity("login")
        } else {
            viewModel.checkLogin()
        }
    }

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
            "register nickname" -> {
                val nicknameBinding = DialogNicknameBinding.inflate(layoutInflater)

                nicknameBinding.btnValidate.setOnClickListener {
                    viewModel.validateNickname(nicknameBinding.etNickname.text.toString())
                }

                MaterialAlertDialogBuilder(this@LaunchActivity)
                    .setTitle(getString(R.string.register_nickname_dialog_title))
                    .setView(nicknameBinding.root)
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        Log.d(TAG, nicknameBinding.etNickname.text.toString())
                        viewModel.updateNickname(nicknameBinding.etNickname.text.toString(), dialogInterface)
                    }
                    .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
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
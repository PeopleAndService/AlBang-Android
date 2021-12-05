package com.pns.albang.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.albang.R
import com.pns.albang.databinding.ActivityMypageBinding
import com.pns.albang.databinding.DialogNicknameBinding
import com.pns.albang.viewmodel.MyPageViewModel

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.view = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        setObserver()
    }

    fun setOnClick(view: View) {
        when (view.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_update_nickname -> {
                viewModel.showDialog("update nickname")
            }
            R.id.btn_my_guestbook -> {
                navigateActivity("my guestbook")
            }
            R.id.btn_open_source_license -> {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source_license))
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
            R.id.btn_logout -> {
                viewModel.showDialog("sign out")
            }
            R.id.btn_withdraw -> {
                viewModel.showDialog("withdraw")
            }
        }
    }

    private fun setObserver() {
        viewModel.showDialog.observe(this) {
            it.getContentIfNotHandled()?.let { type ->
                showDialog(type)
            }
        }

        viewModel.eventTrigger.observe(this) {
            it.getContentIfNotHandled()?.let { type ->
                when (type) {
                    "sign out done" -> {
                        navigateActivity("login")
                    }
                    "withdraw done" -> {
                        navigateActivity("login")
                    }
                }
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

    private fun navigateActivity(where: String) {
        when (where) {
            "my guestbook" -> {
                startActivity(Intent(this, MyGuestbookActivity::class.java))
            }
            "login" -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun showDialog(type: String) {
        when (type) {
            "update nickname" -> {
                val nicknameBinding = DialogNicknameBinding.inflate(layoutInflater)

                nicknameBinding.tvDescription.visibility = View.GONE
                nicknameBinding.etNickname.setText(viewModel.userNickname.value)

                nicknameBinding.btnValidate.setOnClickListener {
                    viewModel.validateNickname(nicknameBinding.etNickname.text.toString())
                }

                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.update_nickname_dialog_title))
                    .setView(nicknameBinding.root)
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        viewModel.updateNickname(nicknameBinding.etNickname.text.toString(), dialogInterface)
                    }
                    .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
            "sign out" -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_confirm_message))
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                            .addOnSuccessListener {
                                viewModel.signOut()
                            }
                            .addOnFailureListener {
                                viewModel.showDialog("sign out fail")
                            }
                            .addOnCompleteListener {
                                dialogInterface.dismiss()
                            }
                    }
                    .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
            "withdraw" -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.withdraw))
                    .setMessage(getString(R.string.withdraw_confirm_message))
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).revokeAccess()
                            .addOnSuccessListener {
                                viewModel.withDraw()
                            }
                            .addOnFailureListener {
                                viewModel.showDialog("withdraw fail")
                            }
                            .addOnCompleteListener {
                                dialogInterface.dismiss()
                            }
                    }
                    .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
            "sign out fail" -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.logout_fail_title))
                    .setMessage(getString(R.string.logout_fail_message))
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
            "withdraw fail" -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.withdraw_fail_title))
                    .setMessage(getString(R.string.withdraw_fail_message))
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}
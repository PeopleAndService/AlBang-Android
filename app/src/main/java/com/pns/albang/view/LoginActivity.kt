package com.pns.albang.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.albang.MainActivity
import com.pns.albang.R
import com.pns.albang.databinding.ActivityLoginBinding
import com.pns.albang.databinding.DialogNicknameBinding
import com.pns.albang.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    private val signInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val googleUserId = account.id
            val email = account.email ?: ""
            val name = account.displayName ?: ""

            googleUserId?.let {
                viewModel.signIn(googleUserId, email, name)
            }
        } catch (e: ApiException) {
            Log.e(TAG, e.toString())
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener {
            viewModel.setEvent("signIn")
        }

        setObserver()
    }

    private fun setObserver() {
        viewModel.eventTrigger.observe(this) {
            it.getContentIfNotHandled()?.let { type ->
                when (type) {
                    "signIn" -> {
                        signIn()
                    }
                }
            }
        }

        viewModel.showDialog.observe(this) {
            it.getContentIfNotHandled()?.let { type ->
                showDialog(type)
            }
        }

        viewModel.loginUser.observe(this) {
            if (it.nickname == null) {
                viewModel.showDialog("register nickname")
            } else {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewModel.validateNicknameResult.observe(this) {
            it.getContentIfNotHandled()?.let { result ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("닉네임 중복확인")
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

    private fun signIn() {
        val mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val googleSignInIntent = mGoogleSignInClient.signInIntent

        signInResult.launch(googleSignInIntent)
    }

    private fun showDialog(type: String) {
        when (type) {
            "login fail" -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.login_fail_dialog_title))
                    .setMessage(getString(R.string.login_fail_dialog_message))
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
            "register nickname" -> {
                val nicknameBinding = DialogNicknameBinding.inflate(layoutInflater)

                nicknameBinding.btnValidate.setOnClickListener {
                    viewModel.validateNickname(nicknameBinding.etNickname.text.toString())
                }

                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.register_nickname_dialog_title))
                    .setView(nicknameBinding.root)
                    .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                        Log.d(TAG, nicknameBinding.etNickname.text.toString())
                        viewModel.updateNickname(nicknameBinding.etNickname.text.toString(), dialogInterface)
                    }
                    .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                        viewModel.updateNickname("", dialogInterface)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    companion object {
        private const val TAG = "LOGIN ACTIVITY"
    }
}
package com.pns.albang

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pns.albang.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }

    companion object {
        private const val TAG = "LOGIN ACTIVITY"
    }
}
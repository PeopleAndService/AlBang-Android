package com.pns.albang

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pns.albang.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        binding.heritageName = "sample text"

        setContentView(binding.root)
    }
}
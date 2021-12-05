package com.pns.albang.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.albang.R
import com.pns.albang.data.MyGuestbook
import com.pns.albang.databinding.ActivityMyGuestbookBinding
import com.pns.albang.databinding.DialogDeleteGuestbookBinding
import com.pns.albang.view.adapter.MyGuestbookAdapter
import com.pns.albang.viewmodel.MyGuestbookViewModel

class MyGuestbookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyGuestbookBinding
    private val viewModel: MyGuestbookViewModel by viewModels()

    private lateinit var myGuestbookAdapter: MyGuestbookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyGuestbookBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.view = this
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.viewModel = viewModel
        initRcView()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }

    private fun initRcView() {
        binding.rcGuestbook.apply {
            myGuestbookAdapter = MyGuestbookAdapter { item -> deleteGuestbook(item) }
            layoutManager = LinearLayoutManager(this@MyGuestbookActivity)
            setHasFixedSize(true)
            adapter = myGuestbookAdapter
        }
    }

    private fun deleteGuestbook(item: MyGuestbook) {
        val dialogBinding = DialogDeleteGuestbookBinding.inflate(layoutInflater)

        dialogBinding.data = item

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_guestbook))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.btn_confirm)) { dialogInterface, _ ->
                viewModel.deleteGuestbook(item, dialogInterface)
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    companion object {
        private const val TAG = "MY GUESTBOOK ACTIVITY"
    }
}
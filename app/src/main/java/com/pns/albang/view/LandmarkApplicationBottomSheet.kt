package com.pns.albang.view

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pns.albang.R
import com.pns.albang.databinding.DialogApplicationLandmarkBinding
import com.pns.albang.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LandmarkApplicationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DialogApplicationLandmarkBinding
    private val viewModel: MainViewModel by activityViewModels()

    private val getPictureFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            Glide.with(this).load(uri).into(binding.ivImage)
            imageUri = uri
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri.let {
                Glide.with(this).load(it).into(binding.ivImage)
            }
        } else {
            requireActivity().contentResolver.delete(imageUri, null, null)
        }
    }

    private lateinit var imageName: String
    private lateinit var imageUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogApplicationLandmarkBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.view = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                dismiss()
            }
            R.id.btn_gallery -> {
                getPictureFromGallery.launch("image/*")
            }
            R.id.btn_camera -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    imageName = "$timeStamp.jpg"
                    imageUri = createImageUri(imageName, "image/jpeg")!!
                    putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                }
                takePicture.launch(intent)
            }
            R.id.btn_application -> {
                val file = getFilePathFromUri(imageUri)
                if (file == null) {
                    viewModel.showDialog("apply failed")
                } else {
                    viewModel.applyLandmark(file, binding.etName.text.toString())
                }
            }
        }
    }

    private fun createImageUri(fileName: String, mimeType: String): Uri? {
        val contentValue = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }
        return requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
    }

    private fun getFilePathFromUri(uri: Uri): File? {
        val fileName = getFileName(uri)
        if (!fileName.isNullOrEmpty()) {
            val copyFile = File("${requireContext().filesDir}${File.separator}$fileName.jpg")
            copy(uri, copyFile)
            return copyFile
        }
        return null
    }

    private fun getFileName(uri: Uri): String? {
        val path = uri.path
        path?.let {
            val cut = it.lastIndexOf('/')
            if (cut != -1) {
                return it.substring(cut + 1)
            }
        }
        return null
    }

    private fun copy(uri: Uri, file: File) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.let { ist ->
                val outputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (ist.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }
                ist.close()
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "Landmark Application Bottom Sheet Dialog"
    }
}
package com.example.storyappsub2.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.storyappsub2.BuildConfig
import com.example.storyappsub2.UserPreference
import com.example.storyappsub2.api.ApiClient
import com.example.storyappsub2.databinding.ActivityAddStoryBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.Date
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var userPreferences: UserPreference
    private lateinit var imageUri: Uri
    private var imageFile: File? = null
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreference.getInstance(applicationContext.dataStore)

        checkAndRequestPermissions()

        binding.buttonCamera.setOnClickListener { launchCamera() }
        binding.buttonGallery.setOnClickListener { launchGallery() }
        binding.buttonAdd.setOnClickListener { submitStory() }
    }
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val listPermissionsNeeded: MutableList<String> = ArrayList()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    Toast.makeText(this, "Izin diperlukan untuk mengakses penyimpanan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compressImageFile(imageFile: File): File {
        val bitmap = BitmapFactory.decodeFile(imageFile.path)
        var compressedFile = imageFile

        if (bitmap != null) {
            try {
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

                compressedFile = File.createTempFile("compressed_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                val fos = FileOutputStream(compressedFile)
                fos.write(outputStream.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
        }

        return compressedFile
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File
        try {
            photoFile = createTempImageFile()
            imageFile = photoFile
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
        imageUri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(intent)
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(imageUri).into(binding.ivAddPhoto)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                Glide.with(this).load(imageUri).into(binding.ivAddPhoto)
                imageFile = File(getRealPathFromURI(imageUri))
            }
        }
    }

    private fun submitStory() {
        val description = binding.edAddDescription.text.toString()
        if (imageFile != null && description.isNotEmpty()) {
            val compressedImageFile = compressImageFile(imageFile!!)
            val requestImageFile = compressedImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                compressedImageFile.name,
                requestImageFile
            )
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

            userPreferences.token.asLiveData().observe(this) { token ->
                token?.let {
                    lifecycleScope.launch {
                        try {
                            val response = ApiClient.apiService.uploadStory("Bearer $token", imageMultipart, descriptionPart)
                            if (!response.error) {
                                Toast.makeText(this@AddStoryActivity, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            } else {
                                Toast.makeText(this@AddStoryActivity, response.message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@AddStoryActivity, "Failed to upload story", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please select an image and fill description", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var result = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    @Throws(IOException::class)
    private fun createTempImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
}

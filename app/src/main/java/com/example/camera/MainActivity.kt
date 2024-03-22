package com.example.camera

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.camera.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private var imageList = mutableListOf<String>()
    private lateinit var imgadapter: recycAdapter

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { bitmap ->
                    lifecycleScope.launch {
                        val imageFile = createImageFile()
                        saveBitmapToFile(imageFile, bitmap)
                        imageList.add(imageFile.absolutePath)
                        imgadapter.notifyItemInserted(imageList.size - 1)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = binding.recyclerview
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        imgadapter = recycAdapter(imageList)
        recyclerView.adapter = imgadapter

        binding.camerabtn.setOnClickListener {
            if (checkCameraPermission()) {
                takePicture()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            }
        }
    }

    // for external storage
//    private suspend fun createImageFile(): File {
//        return withContext(Dispatchers.IO) {
//            val timeStamp: String =
//                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//            val storageDir: File =
//                getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.resolve("MyCameraApp")
//            storageDir.mkdirs()
//            return@withContext File.createTempFile(
//                "JPEG_${timeStamp}_",
//                ".jpg",
//                storageDir
//            )
//        }
//    }
    //for storing in internal memory
    private suspend fun createImageFile(): File {
        return withContext(Dispatchers.IO) {
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File = File(applicationContext.filesDir, "Pictures/MyCameraApp")
            storageDir.mkdirs()
            return@withContext File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        }
    }


    private suspend fun saveBitmapToFile(file: File, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}

//    private val takePictureLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
//                imageBitmap?.let { bitmap ->
//                    val imageFile = createImageFile()
//                    saveBitmapToFile(imageFile, bitmap)
//                    imageList.add(imageFile.absolutePath)
//                    imgadapter.notifyDataSetChanged()
//                }
//            }
//        }

//    private fun takePicture() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        takePictureIntent.resolveActivity(packageManager)?.also {
//            takePictureLauncher.launch(takePictureIntent)
//        }
//
//    }
//    private fun takePicture() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
//                imageBitmap?.let { bitmap ->
//                    val imageFile = createImageFile()
//                    saveBitmapToFile(imageFile, bitmap)
//                    imageList.add(imageFile.absolutePath)
//                    imgadapter.notifyItemInserted(imageList.size - 1)
//                }
//            }
//        }.launch(takePictureIntent)
//    }


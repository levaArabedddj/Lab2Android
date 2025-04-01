package com.example.lab2android

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var takeSelfieButton: Button
    private lateinit var sendSelfieButton: Button
    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageViews)
        takeSelfieButton = findViewById(R.id.takeSelfieButtone)
        sendSelfieButton = findViewById(R.id.sendSelfieButtone)

        takeSelfieButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        sendSelfieButton.setOnClickListener {
            sendEmailWithImage()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri) // ✅ Передаємо URI у камеру
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            File.createTempFile(
                "selfie_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}",
                ".jpg",
                storageDir
            )
        } catch (e: IOException) {
            Toast.makeText(this, "Помилка створення файлу", Toast.LENGTH_SHORT).show()
            null
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(imageUri) // ✅ Завантажуємо збережене фото
        }
    }


    private fun saveImageToFile(bitmap: Bitmap): Uri? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file =
            File(storageDir, "selfie_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg")
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)

        } catch (e: IOException) {
            Toast.makeText(this, "Помилка збереження зображення", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun sendEmailWithImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Спочатку зробіть селфі", Toast.LENGTH_SHORT).show()
            return
        }

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("lion2005gamer@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "ANDROID Арабаджи Лев")
            putExtra(Intent.EXTRA_TEXT, "Посилання на репозиторій до другої лабораторної роботи : https://github.com/levaArabedddj/Lab2Android")
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // ✅ Додаємо права на читання
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Надіслати селфі..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Немає доступного додатку для надсилання пошти", Toast.LENGTH_SHORT).show()
        }
    }

}
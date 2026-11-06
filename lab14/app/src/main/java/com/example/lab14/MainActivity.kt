package com.example.lab14

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mainImageView: ImageView
    private lateinit var recyclerGallery: RecyclerView
    private var currentPhotoUri: Uri? = null
    private val photoList = mutableListOf<File>()

    // Лаунчер для камеры (сохраняет в файл полного размера)
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            // Отображаем только что сделанное фото
            mainImageView.setImageURI(currentPhotoUri)
            // Обновляем галерею
            loadPhotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainImageView = findViewById(R.id.mainImageView)
        recyclerGallery = findViewById(R.id.recyclerGallery)
        val btnCamera = findViewById<Button>(R.id.btnCamera)

        // Настраиваем горизонтальную галерею
        recyclerGallery.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Загружаем ранее сохраненные фото при запуске
        loadPhotos()

        btnCamera.setOnClickListener {
            // Создаем файл для сохранения фото
            val photoFile = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "IMG_${System.currentTimeMillis()}.jpg"
            )

            // 1. Создаем локальную переменную (она точно не null)
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )

            // 2. Сохраняем её в переменную класса (для использования в лаунчере)
            currentPhotoUri = uri

            // 3. Передаем в камеру именно локальную переменную
            takePictureLauncher.launch(uri)
        }
    }

    // Метод для чтения папки с фото и обновления галереи
    private fun loadPhotos() {
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (directory != null && directory.exists()) {
            // Получаем все файлы .jpg и сортируем от новых к старым
            val files = directory.listFiles { file -> file.extension == "jpg" }
            photoList.clear()
            if (files != null) {
                photoList.addAll(files.sortedByDescending { it.lastModified() })
            }
        }

        // Подключаем адаптер
        val adapter = GalleryAdapter(photoList) { file ->
            // При клике на миниатюру показываем фото на весь экран
            mainImageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
        }
        recyclerGallery.adapter = adapter

        // Если есть фото, показываем последнее на главном экране при запуске
        if (photoList.isNotEmpty() && mainImageView.drawable == null) {
            mainImageView.setImageBitmap(BitmapFactory.decodeFile(photoList[0].absolutePath))
        }
    }
}
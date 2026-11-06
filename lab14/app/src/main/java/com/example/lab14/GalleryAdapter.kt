package com.example.lab14

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryAdapter(
    private val photos: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.thumbnailImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = photos[position]
        // Загружаем картинку (уменьшенную для превью, чтобы не забить память)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        holder.image.setImageBitmap(bitmap)

        // Обработка клика по миниатюре
        holder.image.setOnClickListener {
            onClick(file)
        }
    }

    override fun getItemCount(): Int = photos.size
}
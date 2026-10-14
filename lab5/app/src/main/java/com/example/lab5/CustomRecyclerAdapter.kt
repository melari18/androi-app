package com.example.lab5

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(
    private val context: Context,
    private val images: List<Image>
) : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val text: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        holder.text.text = image.name
        holder.image.setImageResource(image.imageRes)

        // Клик по картинке — открытие диалога
        holder.image.setOnClickListener { view ->
            showImageDialog(image.imageRes)
        }
    }

    override fun getItemCount(): Int = images.size

    private fun showImageDialog(imageRes: Int) {
        val dialogView = inflater.inflate(R.layout.dialog_image, null)
        dialogView.findViewById<ImageView>(R.id.dialogImageView).setImageResource(imageRes)

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_title)
            .setView(dialogView)
            .setPositiveButton(R.string.close) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
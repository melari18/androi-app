package com.example.lab5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val images = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setData()

        // ===== RecyclerView (сетка 2×2) =====
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)     // ← НЕ ЗАБЫТЬ
        val recyclerAdapter = CustomRecyclerAdapter(this, images)
        recyclerView.adapter = recyclerAdapter

        // ===== ListView =====
        val listView = findViewById<ListView>(R.id.listView)
        val listViewAdapter = MyListViewAdapter()
        listView.adapter = listViewAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            showImageDialog(images[position].imageRes)
        }
    }

    private fun setData() {
        images.add(Image("rabbit 1", R.drawable.image1))
        images.add(Image("rabbit 2", R.drawable.image2))
        images.add(Image("my rabbit", R.drawable.image3))
        images.add(Image("cute rabbit", R.drawable.image4))
    }

    inner class MyListViewAdapter : BaseAdapter() {
        private val inflater = LayoutInflater.from(this@MainActivity)

        override fun getCount(): Int = images.size
        override fun getItem(position: Int): Any = images[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: inflater.inflate(R.layout.list_item_listview, parent, false)
            val image = view.findViewById<ImageView>(R.id.lvImageView)
            val text = view.findViewById<TextView>(R.id.lvTextView)
            val item = images[position]
            text.text = item.name
            image.setImageResource(item.imageRes)
            image.setOnClickListener { showImageDialog(item.imageRes) }
            return view
        }
    }

    private fun showImageDialog(imageRes: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image, null)
        dialogView.findViewById<ImageView>(R.id.dialogImageView).setImageResource(imageRes)

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title)
            .setView(dialogView)
            .setPositiveButton(R.string.close) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
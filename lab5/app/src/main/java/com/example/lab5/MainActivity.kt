package com.example.lab5

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // ⚠️ ID должен совпадать с activity_main.xml
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CustomRecyclerAdapter(this, images)
        recyclerView.adapter = adapter
    }

    private fun setData() {
        images.add(Image("rabbit 1", R.drawable.image1))
        images.add(Image("rabbit 2", R.drawable.image2))
        images.add(Image("cute rabbit", R.drawable.image3))
        images.add(Image("my rabbit", R.drawable.image4))
    }
}
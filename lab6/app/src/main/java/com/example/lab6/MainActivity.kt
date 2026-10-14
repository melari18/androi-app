package com.example.lab6

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textView = findViewById<TextView>(R.id.textViewAnimated)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val buttonAnim = AnimationUtils.loadAnimation(this, R.anim.tween_anim)
            button.startAnimation(buttonAnim)
        }

        textView.setOnClickListener {
            val textAnim = AnimationUtils.loadAnimation(this, R.anim.textview_anim)
            textView.startAnimation(textAnim)
        }
    }
}
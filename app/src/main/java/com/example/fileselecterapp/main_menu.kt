package com.example.fileselecterapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class main_menu : AppCompatActivity() {

    private lateinit var buttonMB: Button
    private lateinit var buttonMCC: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        buttonMB = findViewById(R.id.buttonMB)
        buttonMCC = findViewById(R.id.buttonMCC)

        buttonMCC.setOnClickListener {
            val intent = Intent(this@main_menu,MainActivity::class.java)
            startActivity(intent)
        }

        buttonMB.setOnClickListener {
            val intent = Intent(this@main_menu,SecondActivity::class.java)
            startActivity(intent)
        }
    }
}
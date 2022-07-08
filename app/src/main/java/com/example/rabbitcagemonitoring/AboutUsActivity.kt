package com.example.rabbitcagemonitoring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val btnBackAbout: ImageButton = findViewById(R.id.back_about)
        btnBackAbout.setOnClickListener {
            val intent = Intent(this@AboutUsActivity, SplashScreenActivity::class.java)
            startActivity(intent)
        }
    }
}
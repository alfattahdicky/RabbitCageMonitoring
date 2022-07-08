package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.material.progressindicator.CircularProgressIndicator

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val btnSplashScreenActivity: Button = findViewById(R.id.button_splash)
        val btnAboutUs: Button = findViewById(R.id.button_about_us)

        btnSplashScreenActivity.setOnClickListener{
            val loadingIndicator: CircularProgressIndicator  = findViewById(R.id.loading)
            val btnSplashScreenActivity: Button = findViewById(R.id.button_splash)
            loadingIndicator.visibility = View.VISIBLE
            btnSplashScreenActivity.visibility = View.GONE

            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        btnAboutUs.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }

    }
}
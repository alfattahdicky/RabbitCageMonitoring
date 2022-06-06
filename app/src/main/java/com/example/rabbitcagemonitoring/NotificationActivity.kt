package com.example.rabbitcagemonitoring

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class NotificationActivity : AppCompatActivity() {
    lateinit var dataTimePref: DataTimePref
    private var TAG: String = "Notification Activity"
    private var dataNotification: ArrayList<DataNotification> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        this.dataTimePref = DataTimePref(applicationContext)


        loadData()


        val listView: ListView = findViewById(R.id.listViewNotification)
        listView.adapter = AdapterListView(this, this.dataNotification)

        backToHome()
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Notification Prefrences", MODE_PRIVATE)

        val gson = Gson()
        val json: String? = sharedPreferences.getString("Notification List", null)
        val type: Type = object : TypeToken<ArrayList<DataNotification?>?>() {}.type

        this.dataNotification =  gson.fromJson(json, type)

        this.dataNotification.reverse()
    }

    private fun backToHome() {
        val backButtonNotification: ImageButton = findViewById(R.id.backButtonNotification)
        backButtonNotification.setOnClickListener {
            val intent = Intent(this@NotificationActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }


}
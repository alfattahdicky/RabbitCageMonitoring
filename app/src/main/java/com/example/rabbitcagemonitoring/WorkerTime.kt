package com.example.rabbitcagemonitoring

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class WorkerTime(val context: Context, val params:WorkerParameters): Worker(context, params) {
    private var TAG = "Worker Time"
    private var data: ArrayList<DataNotification> = ArrayList()

    override fun doWork(): Result {
        NotificationHelper(context).createNotification(
            inputData.getString("title").toString(),
            inputData.getString("description").toString()
        )

        Log.d(TAG, inputData.getString("title").toString())
        Log.d(TAG, inputData.getString("description").toString())

        if(this.data != null) {
            loadData()
        }

        saveDataNotification()

        return Result.success()
    }

    private fun loadData(): ArrayList<DataNotification> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Notification Prefrences", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String? = sharedPreferences.getString("Notification List", null)
        val type: Type = object : TypeToken<java.util.ArrayList<DataNotification?>?>() {}.type

        this.data = gson.fromJson(json, type)

        return this.data

    }

    private fun saveDataNotification() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Notification Prefrences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()

        this.data.add(DataNotification(inputData.getString("title").toString(), inputData.getString("description").toString()))
        val json: String = gson.toJson(this.data)

        Log.d(TAG, json)

        editor.putString("Notification List", json)
        editor.apply()
    }
}
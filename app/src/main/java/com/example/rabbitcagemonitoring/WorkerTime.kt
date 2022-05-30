package com.example.rabbitcagemonitoring

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkerTime(val context: Context, val params:WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        NotificationHelper(context).createNotification(
            inputData.getString("title").toString(),
            inputData.getString("description").toString()
        )
        return Result.success()
    }
}
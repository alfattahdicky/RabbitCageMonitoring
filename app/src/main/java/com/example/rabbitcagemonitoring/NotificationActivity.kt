package com.example.rabbitcagemonitoring

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

const val channelID = "Rabbit Cage Monitoring"
const val titleExtra = "title"
const val descriptionExtra = "description"

class NotificationActivity : AppCompatActivity() {
    private val CHANNEL_ID = "channel_id"
    private lateinit var database: DatabaseReference
    lateinit var dataTimePref: DataTimePref
    private var TAG: String = "Notification Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val backButtonNotification: ImageButton = findViewById(R.id.backButtonNotification)
        val btnNotificationTv: Button = findViewById(R.id.btn_notificationTv)
        val btnNotificationTvTwo: Button = findViewById(R.id.btn_notificationTvTwo)

        dataTimePref = DataTimePref(applicationContext)
        val dataTime = dataTimePref.getPreferences()

        val time = LocalTime.parse(dataTime.eatDrinkTimeOne)

        Log.d(TAG, time.hour.toString())

        Toast.makeText(this, "$dataTime", Toast.LENGTH_SHORT).show()
    }

    private fun showNotificationEatDrink() {
        //  current time
        val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val calender = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 16)
        }

        val title = "Waktu Pembersihan"
        val description = "Waktu pembersihan dilakukan ${calender.timeInMillis}"

        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(descriptionExtra, description)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            0
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calender.timeInMillis,
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, description: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.clock)

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.logo_rabbit)
            .setContentTitle(title)
            .setContentText(description)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val timeId: Long = System.currentTimeMillis()

        with(NotificationManagerCompat.from(this)) {
            notify(timeId.toInt(), builder.build())
        }
    }


}
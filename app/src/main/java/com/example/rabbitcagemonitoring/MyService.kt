package com.example.rabbitcagemonitoring

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.*

class MyService : Service() {

    private val TAG: String = "Service"
    private lateinit var database: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().getReference("DataCage")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val weightListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val getEatWeight = snapshot.child("eatLoadCell").value
                    val getDrinkWeight = snapshot.child("drinkLoadCell").value

                    if(getEatWeight.toString() == 0.toString()) {
                        NotificationHelper(applicationContext).createNotification(
                            "Makanan Kelinci",
                            "Makanan Kosong akan diisi otomatis sesuai waktu yang telah diatur"
                        )
                        showLog("Makanan Kosong")
                    }

                    if(getDrinkWeight.toString() == 0.toString()) {
                        NotificationHelper(applicationContext).createNotification(
                            "Minuman Kelinci",
                            "Minuman Kosong akan diisi otomatis sesuai waktu yang telah diatur"
                        )
                        showLog("Minuman Kosong")
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.toString())
            }
        }

        database.addValueEventListener(weightListener)

        showLog("Service Running")


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun showLog(message: String) {
        Log.d(TAG, message)
    }
}
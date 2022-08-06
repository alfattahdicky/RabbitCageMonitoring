package com.example.rabbitcagemonitoring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyArrayList: ArrayList<DataHistory>
    private var TAG: String = "HistoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyRecyclerView = findViewById(R.id.historylist)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        historyRecyclerView.setHasFixedSize(true)

        historyArrayList = arrayListOf<DataHistory>()
        getHistoryData()
        backToHome()

    }

    private fun backToHome() {
        val btnBackHistory: ImageButton = findViewById(R.id.btn_backHistory)
        btnBackHistory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getHistoryData() {
        database = FirebaseDatabase.getInstance().getReference("DataRiwayat")

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(dataSnapshot in snapshot.children) {
                        val riwayat = dataSnapshot.getValue(DataHistory::class.java)
                        historyArrayList.add(riwayat!!)
                    }
                    historyRecyclerView.adapter = HistoryAdapter(historyArrayList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }

        })
    }
}
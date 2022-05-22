package com.example.rabbitcagemonitoring

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseService {
    private lateinit var database: DatabaseReference

    fun updateData(mapOfValue: Map<String, Any>) {
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        database.updateChildren(mapOfValue).addOnSuccessListener {
            Log.d("Success Update Data", "Data Successfully updated")
        }.addOnFailureListener {
            Log.d("Failed Update Data", "Because $it")
        }
    }
}
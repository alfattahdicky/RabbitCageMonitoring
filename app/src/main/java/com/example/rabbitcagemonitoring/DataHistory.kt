package com.example.rabbitcagemonitoring

data class DataHistory(
    var suhu: Float? = null,
    val kelembaban: Float? = null,
    val waktu: String? = null,
    val beratMakan: Float? = null,
    val beratMinum: Float? = null
)

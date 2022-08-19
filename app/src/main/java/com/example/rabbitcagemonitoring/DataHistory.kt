package com.example.rabbitcagemonitoring

data class DataHistory(
    var suhu: String? = null,
    val kelembaban: String? = null,
    val waktu: String? = null,
    val beratMakan: String? = null,
    val beratMinum: String? = null
)

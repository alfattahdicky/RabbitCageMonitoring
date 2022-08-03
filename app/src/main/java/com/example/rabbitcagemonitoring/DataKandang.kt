package com.example.rabbitcagemonitoring

// Data Cage need to assign realtime firebase

data class DataKandang(
    var temperature: Float ?= null,
    var suhu: Float ?= null,
    var humidity: Float ?= null,
    var kelembaban: Float ?= null,
    var light: Boolean = false,
    var lampu: Boolean = false,
    var fan: Boolean = false,
    var kipas: Boolean = false,
    var firstValueTemperature: Float ?= null,
    var nilaiAwalSuhu: Float ?= null,
    var secondValueTemperature: Float ?= null,
    var nilaiAkhirSuhu: Float ?= null,
    var valueHumidity: Float ?= null,
    var nilaiKelembaban: Float ?= null,
    var firstEatDrinkTime: String ?= null,
    var waktuMakanMinumPertama: String ?= null,
    var secondEatDrinkTime: String ?= null,
    var waktuMakanMinumKedua: String ?= null,
    var thirdEatDrinkTime: String ?= null,
    var waktuMakanMinumKetiga: String ?= null,
    var eatWeight: Int ?= null,
    var beratMakan: Int ?= null,
    var drinkWeight: Int ?= null,
    var beratMinum: Int ?= null,
    var firstCleanerTime: String ?= null,
    var waktuBersihPertama: String ?= null,
    var secondCleanerTime: String ?= null,
    var waktuBersihKedua: String ?= null
)

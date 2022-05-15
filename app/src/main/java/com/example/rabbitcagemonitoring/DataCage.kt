package com.example.rabbitcagemonitoring

// Data Cage need to assign realtime firebase

data class DataCage(
    var temperature: Float ?= null,
    var humidity: Float ?= null,
    var light: Boolean = false,
    var fan: Boolean = false,
    var firstValueTemperature: Float ?= null,
    var secondValueTemperature: Float ?= null,
    var valueHumidity: Float ?= null,
    var firstEatDrinkTime: String ?= null,
    var secondEatDrinkTime: String ?= null,
    var thirdEatDrinkTime: String ?= null,
    var eatWeight: Int ?= null,
    var drinkWeight: Int ?= null,
    var firstCleanerTime: String ?= null,
    var secondCleanerTime: String ?= null
)

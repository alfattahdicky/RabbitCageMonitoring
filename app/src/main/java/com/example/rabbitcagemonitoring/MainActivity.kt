package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity : AppCompatActivity() {
    private var eatDrinkControlMorning: String = ""
    private var eatDrinkControlAfternoon: String = ""
    private var eatDrinkControlNight: String = ""
    private var cleanControlOne: String = ""
    private var cleanControlTwo: String = ""

    private lateinit var eatDrinkButtonMorning: Button
    private lateinit var eatDrinkButtonAfternoon: Button
    private lateinit var eatDrinkButtonNight: Button
    private lateinit var cleanControlButtonOne: Button
    private lateinit var cleanControlButtonTwo: Button

    private lateinit var database: DatabaseReference

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eatDrinkButtonMorning = findViewById(R.id.timeButton_morning)
        eatDrinkButtonAfternoon = findViewById(R.id.timeButton_afternoon)
        eatDrinkButtonNight = findViewById(R.id.timeButton_night)
        cleanControlButtonOne = findViewById(R.id.timeButton_one)
        cleanControlButtonTwo = findViewById(R.id.timeButton_two)

        // get data humidity & temperature
        getHumidityTemperature()

        // function time picker dialog for eat & drink
        val arrEatDrinkButton = arrayOf(eatDrinkButtonMorning, eatDrinkButtonAfternoon, eatDrinkButtonNight)
        for (eatDrinkButton in 0..arrEatDrinkButton.size - 1) timePickers(arrEatDrinkButton[eatDrinkButton])

        // edit & update weight eat & drink
        inputWeightEatAndDrink()

        // time picker for cleaning cage
        val arrCleanButton = arrayOf(cleanControlButtonOne, cleanControlButtonTwo)
        for(cleanButton in 0..arrCleanButton.size - 1) timePickers(arrCleanButton[cleanButton])

        // move notification activity
        val notificationButton: ImageButton = findViewById(R.id.btn_notification)

        notificationButton.setOnClickListener {
            val notificationIntent = Intent(this, NotificationActivity::class.java)
            startActivity(notificationIntent)
        }

        // log range slider
        val rangeSliderTv: RangeSlider = findViewById(R.id.rangeSlider_tv)
        val sliderTv: Slider = findViewById(R.id.slider_tv)

        sliderTv.addOnChangeListener { _, value, _ ->
            Toast.makeText(this, value.toString(), Toast.LENGTH_SHORT).show()
        }

        rangeSliderTv.addOnChangeListener { _, _ , _ ->
            Log.d("first value", (rangeSliderTv.values[0] is Float).toString())
            Log.d("second value", rangeSliderTv.values[1].toString())
        }

        // log database
//        database = FirebaseDatabase.getInstance().getReference("DataCage")
//        val data = DataCage(0.0F, 0.0F, light = false, fan = false,
//            0.0F, 0.0F, 0.0F,
//            "", "", "", 0,
//            0, "", "")
//
//        database.setValue(data).addOnSuccessListener {
//            Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(this, "Failed Saved", Toast.LENGTH_SHORT).show()
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun getHumidityTemperature() {
        val humidityTv: TextView = findViewById(R.id.humidity_tv)
        val temperatureTv: TextView = findViewById(R.id.temperature_tv)

        database = FirebaseDatabase.getInstance().getReference("DataCage")
        database.get().addOnSuccessListener {
            if(it.exists()) {
                val humidity = it.child("humidity").value
                val temperature = it.child("temperature").value

                humidityTv.text = humidity.toString() + " %"
                temperatureTv.text = temperature.toString() + " C"

            }else {
                Toast.makeText(this, "Cannot get Data from database", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun inputWeightEatAndDrink() {
        val inputEatWeightTv: EditText = findViewById(R.id.input_eat)
        val inputDrinkWeightTv: EditText = findViewById(R.id.input_drink)
        val editEatButtonTv: ImageButton = findViewById(R.id.edit_eat_button)
        val editDrinkButtonTv: ImageButton = findViewById(R.id.edit_drink_button)
        val updateEatButtonTv: Button = findViewById(R.id.update_eat_button)
        val updateDrinkButtonTv: Button = findViewById(R.id.update_drink_button)
        val eatTv: TextView = findViewById(R.id.eat_tv)
        val drinkTv: TextView = findViewById(R.id.drink_tv)

        editEatButtonTv.setOnClickListener {
            inputEatWeightTv.visibility = View.VISIBLE
            eatTv.visibility = View.GONE
            updateEatButtonTv.visibility = View.VISIBLE
            editEatButtonTv.visibility = View.GONE
        }

        updateEatButtonTv.setOnClickListener {
            inputEatWeightTv.visibility = View.GONE
            eatTv.text = inputEatWeightTv.text.toString()
            eatTv.visibility = View.VISIBLE
            updateEatButtonTv.visibility = View.GONE
            editEatButtonTv.visibility = View.VISIBLE

            Toast.makeText(this, "Berat makan berhasil di update", Toast.LENGTH_SHORT).show()
        }

        editDrinkButtonTv.setOnClickListener {
            inputDrinkWeightTv.visibility = View.VISIBLE
            drinkTv.visibility = View.GONE
            updateDrinkButtonTv.visibility = View.VISIBLE
            editDrinkButtonTv.visibility = View.GONE
        }

        updateDrinkButtonTv.setOnClickListener {
            inputDrinkWeightTv.visibility = View.GONE
            drinkTv.text = inputDrinkWeightTv.text.toString()
            drinkTv.visibility = View.VISIBLE
            updateDrinkButtonTv.visibility = View.GONE
            editDrinkButtonTv.visibility = View.VISIBLE

            Toast.makeText(this, "Berat minum berhasil di update", Toast.LENGTH_SHORT).show()
        }

    }

    private fun timePickers(button: Button) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        button.setOnClickListener {
            val timePickerDialog =
                TimePickerDialog(this, { _, hourOfDay, minute ->
                    val time: String = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    setTimeEatDrink(button, hourOfDay, time)
                    setTimeCleaner(button, hourOfDay, time)
                }, hour, min, true)
            timePickerDialog.show()
        }
    }

    private fun setTimeCleaner(button: Button, hourOfDay: Int, time: String) {
        val conditionButtonCleanerOne: Boolean = button === cleanControlButtonOne && hourOfDay < 12 && hourOfDay > 0
        val conditionButtonCleanerTwo: Boolean = button === cleanControlButtonTwo && hourOfDay < 24 && hourOfDay > 12

        when {
            conditionButtonCleanerOne -> {
                cleanControlOne = time
                button.text = time
                Toast.makeText(this, "Pembersihan dilakukan pukul $cleanControlOne", Toast.LENGTH_SHORT).show()
            }
            conditionButtonCleanerTwo -> {
                cleanControlTwo = time
                button.text = time
                Toast.makeText(this, "Pembersihan dilakukan pukul $cleanControlTwo", Toast.LENGTH_SHORT).show()
            }
            button == cleanControlButtonOne || button == cleanControlButtonTwo  -> {
                if(!conditionButtonCleanerOne) {
                    cleanControlOne = ""
                    button.text = ""
                }else if (!conditionButtonCleanerTwo) {
                    cleanControlTwo= ""
                    button.text = ""
                }
                Toast.makeText(this, "Salah memilih waktu pembersihan", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("Cleaner", cleanControlOne)
        Log.d("Cleaner", cleanControlTwo)
    }

    private fun setTimeEatDrink(button: Button, hourOfDay: Int, time: String) {
        val conditionButtonMorning: Boolean = button === eatDrinkButtonMorning && hourOfDay < 12 && hourOfDay > 0
        val conditionButtonAfternoon: Boolean = button === eatDrinkButtonAfternoon && hourOfDay < 18 && hourOfDay > 12
        val conditionButtonNight: Boolean = button === eatDrinkButtonNight && hourOfDay < 24 && hourOfDay > 18
        when {
            conditionButtonMorning -> {
                eatDrinkControlMorning = time
                button.text = time

                Toast.makeText(this, "Waktu $eatDrinkControlMorning Pagi hari", Toast.LENGTH_SHORT).show()
            }
            conditionButtonAfternoon -> {
                eatDrinkControlAfternoon = time
                button.text = time

                Toast.makeText(this, "Waktu $eatDrinkControlAfternoon Siang hari", Toast.LENGTH_SHORT).show()
            }
            conditionButtonNight -> {
                eatDrinkControlNight = time
                button.text = time

                Toast.makeText(this, "Waktu $eatDrinkControlNight malam hari", Toast.LENGTH_SHORT).show()
            }
            button == eatDrinkButtonMorning || button == eatDrinkButtonAfternoon || button == eatDrinkButtonNight -> {
                if(!conditionButtonMorning) {
                    eatDrinkControlMorning = ""
                    button.text = ""
                }else if(!conditionButtonAfternoon) {
                    eatDrinkControlAfternoon = ""
                    button.text = ""
                }else if(!conditionButtonNight) {
                    eatDrinkControlNight = ""
                    button.text = ""
                }
                Toast.makeText(this, "Waktu anda yang pilih salah", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("Eat Drink", eatDrinkControlMorning)
        Log.d("Eat Drink", eatDrinkControlAfternoon)
        Log.d("Eat Drink", eatDrinkControlNight)
    }
}


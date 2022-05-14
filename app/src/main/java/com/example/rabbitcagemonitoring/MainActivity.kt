package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eatDrinkButtonMorning = findViewById(R.id.timeButton_morning)
        eatDrinkButtonAfternoon = findViewById(R.id.timeButton_afternoon)
        eatDrinkButtonNight = findViewById(R.id.timeButton_night)
        cleanControlButtonOne = findViewById(R.id.timeButton_one)
        cleanControlButtonTwo = findViewById(R.id.timeButton_two)

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

                    Log.d("hour", hourOfDay.toString())

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
                        else -> {
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
                        }else -> {
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

                }, hour, min, true)
            timePickerDialog.show()
        }
    }
}


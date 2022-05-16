package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.google.firebase.database.*
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

        // get data humidity & temperature from firebase
        getHumidityTemperature()

        // set & update light & fan from firebase
        setUpdateLightAndFan()

        // set & update temperature
        setUpdateTemperature()

        // set & update humidity
        setUpdateHumidity()

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

    private fun setUpdateHumidity() {
        // get id component
        val sliderTv: Slider = findViewById(R.id.slider_tv)

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        // get data
        val humidityListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val valueHumidity = snapshot.child("valueHumidity").value

                    sliderTv.value = (valueHumidity as Long).toFloat()

                    Log.d("Slider", "Success get data humidity slider")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Slider", "Cannot get data humidity slider because $error")
            }
        }
        database.addValueEventListener(humidityListener)

        // update data
        sliderTv.addOnChangeListener { _, _, _ ->
            val sliderValue = sliderTv.value

            val mapValueSlider = mapOf<String, Any>(
                "valueHumidity" to sliderValue
            )

            database.updateChildren(mapValueSlider).addOnSuccessListener {
                Log.d("Slider Update", "Success update value slider to firebase")
            }.addOnFailureListener {
                Log.d("Slider Failed", "Failed Update value slider because $it")
            }
        }
    }

    @SuppressLint("WrongConstant", "ShowToast")
    private fun setUpdateTemperature() {
        // get id component
        val rangeSliderTv: RangeSlider = findViewById(R.id.rangeSlider_tv)

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        // get data from database
        val temperatureListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val dataFirstValue = snapshot.child("firstValueTemperature").value
                    val dataSecondValue = snapshot.child("secondValueTemperature").value

                    rangeSliderTv.setValues((dataFirstValue as Long).toFloat(), (dataSecondValue as Long).toFloat())

                    Toast.makeText(this@MainActivity, "Get data successfully range slider", 2000).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Cannot get data range slider because $error", 3000).show()
            }
        }

        database.addValueEventListener(temperatureListener)

        //update data
        rangeSliderTv.addOnChangeListener { _, _, _ ->
            val values = rangeSliderTv.values

            val mapValueRangeSlider = mapOf<String, Any>(
                "firstValueTemperature" to values[0],
                "secondValueTemperature" to values[1]
            )

            database.updateChildren(mapValueRangeSlider).addOnSuccessListener {
                Log.d("Range Slider", "Success update data")
            }.addOnFailureListener {
                Log.d("Range Slider", "Failed update data because $it")
            }
        }

    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setUpdateLightAndFan() {
        // get id component

        val switchLightTv: SwitchCompat = findViewById(R.id.switchTv_light)
        val textLightTv: TextView = findViewById(R.id.lightTv_condition)
        val lightIconTv: ImageView = findViewById(R.id.lightIcon)

        val switchFanTv: SwitchCompat = findViewById(R.id.switchTv_fan)
        val textFanTv: TextView = findViewById(R.id.fanTv_condition)
        val fanIconTv: ImageView = findViewById(R.id.fanIcon)

        // get Database firebase
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        val readListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                val data = snapshot.getValue(DataCage::class.java)
                if(snapshot.exists()) {
                    val dataLight = snapshot.child("light").value as Boolean
                    val dataFan = snapshot.child("fan").value as Boolean

                    switchLightTv.isChecked = dataLight
                    if(switchLightTv.isChecked) {
                        textLightTv.text = "Menyala"
                        lightIconTv.setImageResource(R.drawable.lamp)
                    }else {
                        textLightTv.text = "Mati"
                        lightIconTv.setImageResource(R.drawable.lamp_off)
                    }

                    switchFanTv.isChecked = dataFan
                    if(switchFanTv.isChecked) {
                        textFanTv.text = "Menyala"
                        fanIconTv.setImageResource(R.drawable.fan_on)
                    }else {
                        textFanTv.text= "Mati"
                        fanIconTv.setImageResource(R.drawable.fan)
                    }
                }

                Toast.makeText(this@MainActivity, "Get data switch success" ,Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Get data failed because $error" ,Toast.LENGTH_LONG).show()
            }
        }

        database.addValueEventListener(readListener)

        switchLightTv.setOnClickListener {
            val light = switchLightTv.isChecked

            val mapDataSwitch = mapOf<String, Any>(
                "light" to light
            )

            database.updateChildren(mapDataSwitch).addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Success Update Fan Switch", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "failed update data because $it", Toast.LENGTH_SHORT).show()
            }
        }

        switchFanTv.setOnClickListener {
            val fan = switchFanTv.isChecked

            val mapDataSwitch = mapOf<String, Any>(
                "fan" to fan
            )

            database.updateChildren(mapDataSwitch).addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Success Update Fan Switch", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "failed update data fan because $it", Toast.LENGTH_SHORT).show()
            }
        }


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

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed get Data from database", Toast.LENGTH_SHORT).show()
        }
    }


    private fun inputWeightEatAndDrink() {
        // get id component
        val inputEatWeightTv: EditText = findViewById(R.id.input_eat)
        val inputDrinkWeightTv: EditText = findViewById(R.id.input_drink)
        val editEatButtonTv: ImageButton = findViewById(R.id.edit_eat_button)
        val editDrinkButtonTv: ImageButton = findViewById(R.id.edit_drink_button)
        val updateEatButtonTv: Button = findViewById(R.id.update_eat_button)
        val updateDrinkButtonTv: Button = findViewById(R.id.update_drink_button)
        val eatTv: TextView = findViewById(R.id.eat_tv)
        val drinkTv: TextView = findViewById(R.id.drink_tv)

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        // get data eat & drink
        database.get().addOnSuccessListener {
            if(it.exists()) {
                val eatValue = it.child("eatWeight").value
                val drinkValue = it.child("drinkWeight").value

                eatTv.text = eatValue.toString()
                drinkTv.text = drinkValue.toString()

                Log.d("Tag","Success Get data eat & drink from firebase")
            }
        }.addOnFailureListener {
            Log.d("Tag", "Failed Get data eat & drink firebase because$it")
        }

        editEatButtonTv.setOnClickListener {
            inputEatWeightTv.visibility = View.VISIBLE
            inputEatWeightTv.setText(eatTv.text)
            eatTv.visibility = View.GONE
            updateEatButtonTv.visibility = View.VISIBLE
            editEatButtonTv.visibility = View.GONE
        }

        updateEatButtonTv.setOnClickListener {
            val valueEat = (inputEatWeightTv.text.toString()).toInt()

            val mapValueEat = mapOf<String, Int>(
                "eatWeight" to valueEat
            )

            database.updateChildren(mapValueEat).addOnSuccessListener {
                inputEatWeightTv.visibility = View.GONE
                eatTv.text = valueEat.toString()
                eatTv.visibility = View.VISIBLE
                updateEatButtonTv.visibility = View.GONE
                editEatButtonTv.visibility = View.VISIBLE

                Toast.makeText(this, "Berat makan berhasil di update", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d("Eat Weight", "Cannot update eat weigth $it")
            }
        }

        editDrinkButtonTv.setOnClickListener {
            inputDrinkWeightTv.visibility = View.VISIBLE
            inputDrinkWeightTv.setText(drinkTv.text)
            drinkTv.visibility = View.GONE
            updateDrinkButtonTv.visibility = View.VISIBLE
            editDrinkButtonTv.visibility = View.GONE
        }

        updateDrinkButtonTv.setOnClickListener {

            val valueDrink = (inputDrinkWeightTv.text.toString()).toInt()

            val mapValueDrink = mapOf<String, Int>(
                "eatWeight" to valueDrink
            )

            database.updateChildren(mapValueDrink).addOnSuccessListener {
                inputDrinkWeightTv.visibility = View.GONE
                drinkTv.text = valueDrink.toString()
                drinkTv.visibility = View.VISIBLE
                updateDrinkButtonTv.visibility = View.GONE
                editDrinkButtonTv.visibility = View.VISIBLE

                Toast.makeText(this, "Berat minum berhasil di update", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d("Drink Weight", "Cannot update eat weight $it")
            }
        }

    }

    private fun timePickers(button: Button) {
        // get calender
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        // get data time from firebase
        getDataTime(button)

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

    private fun getDataTime(button: Button) {
        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        val getDataTimePicker = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val firstEatDrinkTime = snapshot.child("firstEatDrinkTime").value
                    val secondEatDrinkTime = snapshot.child("secondEatDrinkTime").value
                    val thirdEatDrinkTime = snapshot.child("thirdEatDrinkTime").value
                    val firstCleanerTime = snapshot.child("firstCleanerTime").value
                    val secondCleanerTime = snapshot.child("secondCleanerTime").value

                    when (button) {
                        eatDrinkButtonMorning -> eatDrinkButtonMorning.text = firstEatDrinkTime.toString()
                        eatDrinkButtonAfternoon -> eatDrinkButtonAfternoon.text = secondEatDrinkTime.toString()
                        eatDrinkButtonNight -> eatDrinkButtonNight.text = thirdEatDrinkTime.toString()
                        cleanControlButtonOne -> cleanControlButtonOne.text = firstCleanerTime.toString()
                        else -> cleanControlButtonTwo.text = secondCleanerTime.toString()
                    }

                    Log.d("Get data Time picker", "Success get data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("Get data Time picker", "Failed get data time picker because $error")
            }

        }

        database.addValueEventListener(getDataTimePicker)
    }

    private fun setTimeCleaner(button: Button, hourOfDay: Int, time: String) {
        val conditionButtonCleanerOne: Boolean = button === cleanControlButtonOne && hourOfDay < 12 && hourOfDay > 0
        val conditionButtonCleanerTwo: Boolean = button === cleanControlButtonTwo && hourOfDay < 24 && hourOfDay > 12

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        when {
            conditionButtonCleanerOne -> {
                cleanControlOne = time
                button.text = cleanControlOne
                Toast.makeText(this, "Pembersihan dilakukan pukul $cleanControlOne", Toast.LENGTH_SHORT).show()
            }
            conditionButtonCleanerTwo -> {
                cleanControlTwo = time
                button.text = cleanControlTwo
                Toast.makeText(this, "Pembersihan dilakukan pukul $cleanControlTwo", Toast.LENGTH_SHORT).show()
            }
            else -> {
                if(!conditionButtonCleanerOne && button == cleanControlButtonOne) {
                    cleanControlOne = ""
                    button.text = cleanControlOne
                }else if (!conditionButtonCleanerTwo && button == cleanControlButtonTwo) {
                    cleanControlTwo= ""
                    button.text = cleanControlTwo
                }
                Toast.makeText(this, "Salah memilih waktu pembersihan", Toast.LENGTH_SHORT).show()
            }
        }
        val mapCleanControl = mapOf<String, String>(
            "firstCleanerTime" to cleanControlOne,
            "secondCleanerTime" to cleanControlTwo
        )

        database.updateChildren(mapCleanControl).addOnSuccessListener {
            Log.d("Update data", "Success update data clean control")
        }.addOnFailureListener {
            Log.d("Updata data", "Failed update data clean control because $it")
        }

    }

    private fun setTimeEatDrink(button: Button, hourOfDay: Int, time: String) {
        val conditionButtonMorning: Boolean = button === eatDrinkButtonMorning && hourOfDay < 12 && hourOfDay > 0
        val conditionButtonAfternoon: Boolean = button === eatDrinkButtonAfternoon && hourOfDay < 18 && hourOfDay > 12
        val conditionButtonNight: Boolean = button === eatDrinkButtonNight && hourOfDay < 24 && hourOfDay > 18

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataCage")

        when {
            conditionButtonMorning -> {
                eatDrinkControlMorning = time
                button.text = eatDrinkControlMorning

                Toast.makeText(this, "Waktu $eatDrinkControlMorning Pagi hari", Toast.LENGTH_SHORT).show()
            }
            conditionButtonAfternoon -> {
                eatDrinkControlAfternoon = time
                button.text = eatDrinkControlAfternoon

                Toast.makeText(this, "Waktu $eatDrinkControlAfternoon Siang hari", Toast.LENGTH_SHORT).show()
            }
            conditionButtonNight -> {
                eatDrinkControlNight = time
                button.text = eatDrinkControlNight

                Toast.makeText(this, "Waktu $eatDrinkControlNight malam hari", Toast.LENGTH_SHORT).show()
            }
            else -> {
                if(!conditionButtonMorning && button == eatDrinkButtonMorning ) {
                    eatDrinkControlMorning = ""
                    button.text = eatDrinkControlMorning
                }else if(!conditionButtonAfternoon && button == eatDrinkButtonAfternoon) {
                    eatDrinkControlAfternoon = ""
                    button.text = eatDrinkControlMorning
                }else if(!conditionButtonNight && button == eatDrinkButtonNight) {
                    eatDrinkControlNight = ""
                    button.text = eatDrinkControlMorning
                }
                Toast.makeText(this, "Waktu anda yang pilih salah", Toast.LENGTH_SHORT).show()
            }
        }

        val mapEatDrinkControl = mapOf<String, String>(
            "firstEatDrinkTime" to eatDrinkControlMorning,
            "secondEatDrinkTime" to eatDrinkControlAfternoon,
            "thirdEatDrinkTime" to eatDrinkControlNight
        )

        Log.d("Map Eat Drink Control", "$mapEatDrinkControl")

        database.updateChildren(mapEatDrinkControl).addOnSuccessListener {
            Log.d("Update data eatDrinkControl", "Success update data")
        }.addOnFailureListener {
            Log.d("Update data eatDrinkControl", "Failed update data because $it")
        }

    }
}


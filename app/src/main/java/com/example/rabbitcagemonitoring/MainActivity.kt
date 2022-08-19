package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.work.*
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.google.firebase.database.*
import java.lang.Error
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(){

    private var TAG: String = "MainActivitys"

    private var eatDrinkControlMorning: String = ""
    private var eatDrinkControlAfternoon: String = ""
    private var eatDrinkControlNight: String = ""
    private var cleanControlOne: String = ""
    private var cleanControlTwo: String = ""

    private lateinit var morningTime: Button
    private lateinit var afternoonTime: Button
    private lateinit var nightTime: Button
    private lateinit var cleanControlButtonOne: Button
    private lateinit var cleanControlButtonTwo: Button
    lateinit var dataTimePref: DataTimePref


    private lateinit var database: DatabaseReference

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataTimePref = DataTimePref(this)

        val intent = Intent(this@MainActivity, MyService::class.java)
        startService(intent)

        // Update time for regards
        toolbarUpdateTime()

        // get time for display time
        displayTime()

        // get data humidity & temperature from firebase
        getHumidityTemperature()

        // set & update light & fan from firebase
        setUpdateLightAndFan()

        // set & update temperature
        setUpdateTemperature()

        // set & update humidity
        setUpdateHumidity()

        // edit & update weight eat & drink
        inputWeightEatAndDrink()

        // edit & update time eat & drink
        timePickersEatDrink()

        // edit & update time cleaner
        timeCleanerEatDrink()

        // set & update eat & drink switch from firebase
        setUpdateEatDrink()

        // set & update switch cleaner from firebase
        setUpdateCleaner()

        // move notification activity
        moveNotificationActivity()

        // back to splash screen
        backAboutActivity()

        // move to history
        historyActivity()

    }

    private fun setUpdateCleaner() {
        val switchCleanTv: SwitchCompat = findViewById(R.id.switchTv_clean)
        val textCleanTv: TextView = findViewById(R.id.cleanTv_condition)
        // get Database firebase
        database = FirebaseDatabase.getInstance().getReference("DataKandang")

        val readListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val dataClean = snapshot.child("servoPembersih").value as Boolean

                    switchCleanTv.isChecked = dataClean
                    if(switchCleanTv.isChecked) {
                        textCleanTv.text = "Menyala"
                    }else {
                        textCleanTv.text = "Mati"
                    }
                }
                toast("Get data switch success")
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Get data failed because $error")
            }
        }
        database.addValueEventListener(readListener)

        switchCleanTv.setOnClickListener {
            val clean = switchCleanTv.isChecked
            val mode = "manual"

            val mapDataSwitch = mapOf<String, Any>(
                "servoPembersih" to clean,
                "mode" to mode
            )

            database.updateChildren(mapDataSwitch).addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Success Update clean Switch", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "failed update data because $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpdateEatDrink() {
        val switchEatTv: SwitchCompat = findViewById(R.id.switchTv_eat)
        val textEatTv: TextView = findViewById(R.id.eatTv_condition)

        val switchDrinkTv: SwitchCompat = findViewById(R.id.switchTv_drink)
        val textDrinkTv: TextView = findViewById(R.id.drinkTv_condition)

        // get Database firebase
        database = FirebaseDatabase.getInstance().getReference("DataKandang")

        val readListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val dataEat = snapshot.child("servoMakan").value as Boolean
                    val dataDrink = snapshot.child("pumpMinum").value as Boolean

                    switchEatTv.isChecked = dataEat
                    if(switchEatTv.isChecked) {
                        textEatTv.text = "Menyala"
                    }else {
                        textEatTv.text = "Mati"
                    }

                    switchDrinkTv.isChecked = dataDrink
                    if(switchDrinkTv.isChecked) {
                        textDrinkTv.text = "Menyala"
                    }else {
                        textDrinkTv.text= "Mati"
                    }
                }
                toast("Get data switch success")
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Get data failed because $error")
            }
        }
        database.addValueEventListener(readListener)

        switchEatTv.setOnClickListener {
            val eat = switchEatTv.isChecked
            val mode = "manual"

            val mapDataSwitch = mapOf<String, Any>(
                "servoMakan" to eat,
                "mode" to mode
            )

            database.updateChildren(mapDataSwitch).addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Success Update eat Switch", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "failed update data because $it", Toast.LENGTH_SHORT).show()
            }
        }

        switchDrinkTv.setOnClickListener {
            val drink = switchDrinkTv.isChecked
            val mode = "manual"

            val mapDataSwitch = mapOf<String, Any>(
                "pumpMinum" to drink,
                "mode" to mode
            )
            database.updateChildren(mapDataSwitch).addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Success Update Drink Switch", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "failed update data drink because $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun historyActivity() {
        val btnHistory: ImageButton = findViewById(R.id.btn_history)

        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun backAboutActivity() {
        val btnBackSplash: ImageButton = findViewById(R.id.back_main)

        btnBackSplash.setOnClickListener {
            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun parseTime(dataTime: String): LocalTime {
        return LocalTime.parse(dataTime)
    }

    private fun moveNotificationActivity() {
        val notificationButton: ImageButton = findViewById(R.id.btn_notification)

        notificationButton.setOnClickListener {
            val notificationIntent = Intent(this, NotificationActivity::class.java)
            startActivity(notificationIntent)
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun displayTime() {
        val dayTv: TextView = findViewById(R.id.dayView)
        val timeTv: TextView = findViewById(R.id.timeView)

        val arrayMonth = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
            "Agustus", "September", "November", "Desember")
        val arrayDay = arrayOf("Senin","Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Minggu")
        val currentTime = Calendar.getInstance()
        val year = currentTime.get(Calendar.YEAR)
        val month = currentTime.get(Calendar.MONTH)
        val dayMonth = currentTime.get(Calendar.DAY_OF_MONTH)
        val day = currentTime.get(Calendar.WEEK_OF_MONTH)

        Log.d(TAG, dayMonth.toString() + "day")
        Log.d(TAG, day.toString())

        val handler = Handler()
        handler.post(object: Runnable {
            override fun run() {
                val clockFormat = SimpleDateFormat("HH:mm:ss")
                timeTv.text = clockFormat.format(Date())

                handler.postDelayed(this, 1000)
            }
        })
        dayTv.text = "$dayMonth ${arrayMonth[month]} $year"
    }

    @SuppressLint("SetTextI18n")
    private fun toolbarUpdateTime() {
        val tvIconTime: ImageView = findViewById(R.id.icon_day)
        val textTime: TextView = findViewById(R.id.text_day)
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        when (hour) {
            in 0..11 -> {
                tvIconTime.setImageResource(R.drawable.sun_cloud)
                textTime.text = "Selamat Pagi"
            }
            in 12..17 -> {
                tvIconTime.setImageResource(R.drawable.sun)
                textTime.text = "Selamat Siang"
            }
            in 18..23 -> {
                tvIconTime.setImageResource(R.drawable.moon)
                textTime.text = "Selamat Malam"
            }
        }
    }

    private fun setUpdateHumidity() {
        // get id component
        val rangeSliderKelembabanTv: RangeSlider = findViewById(R.id.rangeSliderKelembaban_tv)

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataKandang")

        val humidityListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val dataFirstValue = snapshot.child("nilaiAwalKelembaban").value
                    val dataSecondValue = snapshot.child("nilaiAkhirKelembaban").value

                    rangeSliderKelembabanTv.setValues((dataFirstValue as Long).toFloat(), (dataSecondValue as Long).toFloat())

                    Log.d(TAG, "Get data successfully range slider")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Cannot get data range slider because $error")
            }
        }
        database.addValueEventListener(humidityListener)

        //update data
        rangeSliderKelembabanTv.addOnChangeListener { _, _, _ ->
            val values = rangeSliderKelembabanTv.values

            val mapValueRangeSlider = mapOf<String, Any>(
                "nilaiAwalKelembaban" to values[0],
                "nilaiAkhirKelembaban" to values[1]
            )

            database.updateChildren(mapValueRangeSlider).addOnSuccessListener {
                Log.d("Range Slider", "Success update data")
            }.addOnFailureListener {
                Log.d("Range Slider", "Failed update data because $it")
            }
        }
    }

    @SuppressLint("WrongConstant", "ShowToast")
    private fun setUpdateTemperature() {
        // get id component
        val rangeSliderTv: RangeSlider = findViewById(R.id.rangeSlider_tv)

        // database instance
        database = FirebaseDatabase.getInstance().getReference("DataKandang")

        // get data from database
        val temperatureListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val dataFirstValue = snapshot.child("nilaiAwalSuhu").value
                    val dataSecondValue = snapshot.child("nilaiAkhirSuhu").value

                    rangeSliderTv.setValues((dataFirstValue as Long).toFloat(), (dataSecondValue as Long).toFloat())

                    Log.d(TAG, "Get data successfully range slider")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Cannot get data range slider because $error")
            }
        }
        database.addValueEventListener(temperatureListener)

        //update data
        rangeSliderTv.addOnChangeListener { _, _, _ ->
            val values = rangeSliderTv.values

            val mapValueRangeSlider = mapOf<String, Any>(
                "nilaiAwalSuhu" to values[0],
                "nilaiAkhirSuhu" to values[1]
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
        database = FirebaseDatabase.getInstance().getReference("DataKandang")

        val readListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                val data = snapshot.getValue(DataCage::class.java)
                if(snapshot.exists()) {
                    val dataLight = snapshot.child("lampu").value as Boolean
                    val dataFan = snapshot.child("kipas").value as Boolean

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
                toast("Get data switch success")
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Get data failed because $error")
            }
        }
        database.addValueEventListener(readListener)

        switchLightTv.setOnClickListener {
            val light = switchLightTv.isChecked
            val mode = "manual"

            val mapDataSwitch = mapOf<String, Any>(
                "lampu" to light,
                "mode" to mode
            )

            database.updateChildren(mapDataSwitch).addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Success Update Fan Switch", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "failed update data because $it", Toast.LENGTH_SHORT).show()
            }
        }

        switchFanTv.setOnClickListener {
            val fan = switchFanTv.isChecked
            val mode = "manual"

            val mapDataSwitch = mapOf<String, Any>(
                "kipas" to fan,
                "mode" to mode
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

        database = FirebaseDatabase.getInstance().getReference("DataKandang")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val humidity = snapshot.child("kelembaban").value
                    val temperature = snapshot.child("suhu").value

                    humidityTv.text = humidity.toString() + " %"
                    temperatureTv.text = temperature.toString() + " C"

                    Log.d(TAG, "Success get Data Humidity & temperature")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed Get data Humidity Temperature")
            }

        })
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
        database = FirebaseDatabase.getInstance().getReference("DataKandang")

        // get data eat & drink
        database.get().addOnSuccessListener {
            if(it.exists()) {
                val eatValue = it.child("beratMakan").value
                val drinkValue = it.child("beratMinum").value

                eatTv.text = eatValue.toString()
                drinkTv.text = drinkValue.toString()

                Log.d(TAG,"Success Get data eat & drink from firebase")
            }
        }.addOnFailureListener {
            Log.d(TAG, "Failed Get data eat & drink firebase because$it")
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
                "beratMakan" to valueEat
            )

            database.updateChildren(mapValueEat).addOnSuccessListener {
                inputEatWeightTv.visibility = View.GONE
                eatTv.text = valueEat.toString()
                eatTv.visibility = View.VISIBLE
                updateEatButtonTv.visibility = View.GONE
                editEatButtonTv.visibility = View.VISIBLE

                toast("Berat makan berhasil di update")
            }.addOnFailureListener {
                toast("Cannot update eat weight $it")
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
                "beratMinum" to valueDrink
            )

            database.updateChildren(mapValueDrink).addOnSuccessListener {
                inputDrinkWeightTv.visibility = View.GONE
                drinkTv.text = valueDrink.toString()
                drinkTv.visibility = View.VISIBLE
                updateDrinkButtonTv.visibility = View.GONE
                editDrinkButtonTv.visibility = View.VISIBLE

                toast("Berat minum berhasil di update")
            }.addOnFailureListener {
                toast("Gagal Update eat Weight")
            }
        }
    }

    private fun handleClickButton(view: Button, time: String = ""){
        val idMorningButton = 2131362279
        val idAfternoonButton = 2131362278
        val idNightButton = 2131362280
        val idCleanerOne = 2131362281
        val idCleanerTwo = 2131362282

        Log.d(TAG, view.id.toString())
        when {
            idMorningButton == view.id && view.isClickable -> this.eatDrinkControlMorning = time
            idAfternoonButton == view.id && view.isClickable -> this.eatDrinkControlAfternoon = time
            idNightButton == view.id && view.isClickable ->  this.eatDrinkControlNight = time
            idCleanerOne == view.id && view.isClickable -> this.cleanControlOne = time
            idCleanerTwo == view.id && view.isClickable -> this.cleanControlTwo = time
        }
        val dataTime = DataTime(
            this.eatDrinkControlMorning,
            this.eatDrinkControlAfternoon,
            this.eatDrinkControlNight,
            this.cleanControlOne,
            this.cleanControlTwo
        )
        dataTimePref.setPreferences(dataTime)
        Log.d(TAG, view.id.toString())
        Log.d(TAG, dataTime.toString())

        try {
            database = FirebaseDatabase.getInstance().getReference("DataKandang")

            val mapOfEatDrink = mapOf<String, String>(
                "waktuMakanMinumPertama" to this.eatDrinkControlMorning,
                "waktuMakanMinumKedua" to this.eatDrinkControlAfternoon,
                "waktuMakanMinumKetiga" to this.eatDrinkControlNight
            )

            val mapOfCleaner = mapOf<String, String>(
                "waktuBersihPertama" to this.cleanControlOne,
                "waktuBersihKedua" to this.cleanControlTwo
            )

            updateMapToFirebase(mapOfEatDrink, "Succes Update Eat Drink Time to Firebase")
            updateMapToFirebase(mapOfCleaner, "Success Update Cleaner Time to Firebase")


        }catch(error: Error) {
            Log.d(TAG, "Failed Update Data $error")
        }
    }

    private fun setTimeToMillis(time: String): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, parseTime(time).hour)
            set(Calendar.MINUTE, parseTime(time).minute)
            set(Calendar.SECOND, 0)
        }

        return cal.timeInMillis / 1000L
    }

    private fun updateMapToFirebase(mapOfVariable: Map<String, String>, success: String, failed: String = "Failed Update") {
        database.updateChildren(mapOfVariable).addOnSuccessListener {
            Log.d(TAG, success)
        }.addOnFailureListener {
            Log.d(TAG, "$failed because $it")
        }
    }

    private fun setOnClick(button: Button) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        button.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this@MainActivity,
                getTimePickerListener(button), hour, min, true)
            timePickerDialog.show()
        }
    }

    private fun workRequest(time: Long, titleNotif: String, descriptionNotif: String): Operation {
        val constraints = Constraints.Builder()
            .setRequiresDeviceIdle(false)
            .build()

        val myWorkRequest = OneTimeWorkRequestBuilder<WorkerTime>()
            .setInitialDelay(time, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setInputData(workDataOf(
                "title" to titleNotif,
                "description" to descriptionNotif
            ))
            .build()
        return WorkManager.getInstance(applicationContext).enqueue(myWorkRequest)
    }

    private fun getTimePickerListener(button: Button): TimePickerDialog.OnTimeSetListener {
        return TimePickerDialog.OnTimeSetListener{_, hourOfDay, minute ->
            val time = "%02d:%02d".format(hourOfDay, minute)
            when {
                button === this.morningTime && hourOfDay < 12 && hourOfDay >= 0 -> {
                    handleClickButton(button, time)
                    workRequest(setTimeCalendar(parseTime(time).hour, parseTime(time).minute), "Waktu Makan Pagi",
                    "Saatnya kelinci anda makan pukul $time")
                    button.text = time
                }
                button === this.afternoonTime && hourOfDay < 18 && hourOfDay >= 12 -> {
                    handleClickButton(button, time)
                    workRequest(setTimeCalendar(parseTime(time).hour, parseTime(time).minute), "Waktu Makan Siang",
                        "Saatnya kelinci anda makan pukul $time")
                    button.text = time
                }
                button === this.nightTime && hourOfDay <= 24 && hourOfDay >= 18 -> {
                    handleClickButton(button, time)
                    button.text = time
                    workRequest(setTimeCalendar(parseTime(time).hour, parseTime(time).minute),"Waktu Makan Malam",
                        "Saatnya kelinci anda makan pukul $time")
                }
                else -> {
                    toast("Atur waktu anda kembali!")
                }
            }

        }
    }
    private fun setTimeCalendar(hour: Int, min: Int): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
        }
        val today = Calendar.getInstance()
        return (cal.timeInMillis / 1000L) - (today.timeInMillis / 1000L)
    }

    private fun toast(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }

     private fun timePickersEatDrink(){
         this.morningTime = findViewById(R.id.timeButton_morning)
         this.afternoonTime = findViewById(R.id.timeButton_afternoon)
         this.nightTime = findViewById(R.id.timeButton_night)

         // load shared preferences
         if(dataTimePref.preference.contains(DataTimePref.EATDRINK_TIME_ONE)) {
             val getDataTime = dataTimePref.getPreferences()
             this.morningTime.text = getDataTime.eatDrinkTimeOne
             this.afternoonTime.text = getDataTime.eatDrinkTimeTwo
             this.nightTime.text = getDataTime.eatDrinkTimeThree

             this.eatDrinkControlMorning = getDataTime.eatDrinkTimeOne.toString()
             this.eatDrinkControlAfternoon = getDataTime.eatDrinkTimeTwo.toString()
             this.eatDrinkControlNight = getDataTime.eatDrinkTimeThree.toString()
             toast("Load Data")
         }

         // set time
         setOnClick(this.morningTime)
         setOnClick(this.afternoonTime)
         setOnClick(this.nightTime)
     }

    private fun timeCleanerEatDrink() {
        this.cleanControlButtonOne = findViewById(R.id.timeButton_one)
        this.cleanControlButtonTwo = findViewById(R.id.timeButton_two)

        if(dataTimePref.preference.contains(DataTimePref.CLEANER_TIME_ONE)) {
            val getDataCleaner = dataTimePref.getPreferences()

            this.cleanControlButtonOne.text = getDataCleaner.cleanerTimeOne
            this.cleanControlButtonTwo.text = getDataCleaner.cleanerTimeTwo
            this.cleanControlOne = getDataCleaner.cleanerTimeOne.toString()
            this.cleanControlTwo = getDataCleaner.cleanerTimeTwo.toString()
        }

        // set time
        setOnClickCleaner(this.cleanControlButtonOne)
        setOnClickCleaner(this.cleanControlButtonTwo)
    }

    private fun setOnClickCleaner(button: Button) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        button.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this@MainActivity, getTimePickerListenerCleaner(button),
                hour, minute, true)
            timePickerDialog.show()
        }
    }

    private fun getTimePickerListenerCleaner(button: Button): TimePickerDialog.OnTimeSetListener{
        return TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val time = "%02d:%02d".format(hourOfDay, minute)
            if(button === this.cleanControlButtonOne && hourOfDay < 12 && hourOfDay >= 0) {
                handleClickButton(button, time)
                workRequest(setTimeCalendar(parseTime(time).hour, parseTime(time).minute), "Waktu Pembersihan di Pagi hari",
                    "Saatnya melakukan bersih-bersih pukul $time")
                button.text = time
            } else if (button === this.cleanControlButtonTwo && hourOfDay >= 12 && hourOfDay < 24) {
                handleClickButton(button, time)
                workRequest(setTimeCalendar(parseTime(time).hour, parseTime(time).minute), "Waktu Pembersihan di Sore hari",
                    "Saatnya melakukan bersih-bersih pukul $time")
                button.text = time
            } else {
                toast("Atur Waktu Pembersihan Anda Kembali!")
            }
        }
    }
}




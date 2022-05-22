package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class DataTimePref(context: Context) {
    companion object {
        const val SP_NAME = "timePref"
        var EATDRINK_TIME_ONE = "eatdrinktimeone"
        var EATDRINK_TIME_TWO = "eatdrinktimetwo"
        var EATDRINK_TIME_THREE = "eatdrinktimethree"
        var CLEANER_TIME_ONE = "cleanertimeone"
        var CLEANER_TIME_TWO = "cleanertimetwo"
    }

    val preference: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    @SuppressLint("CommitPrefEdits")
    fun setPreferences(dataTime: DataTime) {
        val prefEditor = preference.edit()
        prefEditor.putString(EATDRINK_TIME_ONE, dataTime.eatDrinkTimeOne)
        prefEditor.putString(EATDRINK_TIME_TWO, dataTime.eatDrinkTimeTwo)
        prefEditor.putString(EATDRINK_TIME_THREE, dataTime.eatDrinkTimeThree)
        prefEditor.putString(CLEANER_TIME_ONE, dataTime.cleanerTimeOne)
        prefEditor.putString(CLEANER_TIME_TWO, dataTime.cleanerTimeTwo)
        prefEditor.apply()
    }

    fun getPreferences(): DataTime {
        val dataTime = DataTime()
        dataTime.eatDrinkTimeOne = preference.getString(EATDRINK_TIME_ONE, "")
        dataTime.eatDrinkTimeTwo = preference.getString(EATDRINK_TIME_TWO, "")
        dataTime.eatDrinkTimeThree = preference.getString(EATDRINK_TIME_THREE, "")
        dataTime.cleanerTimeOne = preference.getString(CLEANER_TIME_ONE, "")
        dataTime.cleanerTimeTwo = preference.getString(CLEANER_TIME_TWO, "")

        return dataTime
    }
}
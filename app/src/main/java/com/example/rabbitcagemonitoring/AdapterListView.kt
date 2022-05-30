package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AdapterListView(private val context: Activity, private val arrayList: ArrayList<DataNotification>):
    ArrayAdapter<DataNotification>(context, R.layout.acitivity_listview, arrayList) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.acitivity_listview ,null)

        val title: TextView = view.findViewById(R.id.listview_title)
        val description: TextView = view.findViewById(R.id.listview_description)

        title.text = arrayList[position].title
        description.text = arrayList[position].description

        return view
    }

}
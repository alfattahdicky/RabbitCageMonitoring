package com.example.rabbitcagemonitoring

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class HistoryAdapter(private val historyList: ArrayList<DataHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.waktu.text = currentItem.waktu
        holder.suhu.text = "Suhu : ${currentItem.suhu} C"
        holder.kelembaban.text = "Kelembaban: ${currentItem.kelembaban} %"
        holder.beratMakan.text = "Berat Makan: ${currentItem.beratMakan} gr"
        holder.beratMinum.text = "Berat Minum: ${currentItem.beratMinum} ml"
    }

    override fun getItemCount(): Int {
        return this.historyList.size
    }

    class HistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val waktu: TextView = itemView.findViewById(R.id.history_time)
        val suhu : TextView = itemView.findViewById(R.id.history_temperature)
        val kelembaban: TextView = itemView.findViewById(R.id.history_humidity)
        val beratMakan: TextView = itemView.findViewById(R.id.history_food)
        val beratMinum: TextView = itemView.findViewById(R.id.history_water)
    }

}
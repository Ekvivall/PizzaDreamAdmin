package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Model.PizzeriaModel
import com.sokol.pizzadreamadmin.R

class PizzeriaAdapter(private val items: List<PizzeriaModel>, private val context: Context) :
    RecyclerView.Adapter<PizzeriaAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textAddress: TextView = view.findViewById(R.id.text_address)
        var textWorkingHours: TextView = view.findViewById(R.id.text_working_hours)
        var textCoordinate: TextView = view.findViewById(R.id.text_coordinate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_pizzeria_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val addressItem = items[position]
        holder.textAddress.text = addressItem.name
        holder.textWorkingHours.text =
            StringBuilder("Час роботи: ").append(addressItem.scheduleWork)
        holder.textCoordinate.text =
            StringBuilder("Координати (").append(addressItem.lat).append("; ")
                .append(addressItem.lng).append(")")
    }
}
package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.CategoryClick
import com.sokol.pizzadreamadmin.EventBus.PizzeriaClick
import com.sokol.pizzadreamadmin.EventBus.UpdateCategoryClick
import com.sokol.pizzadreamadmin.EventBus.UpdatePizzeriaClick
import com.sokol.pizzadreamadmin.Model.PizzeriaModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class PizzeriaAdapter(private var items: List<PizzeriaModel>, private val context: Context) :
    RecyclerView.Adapter<PizzeriaAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var textAddress: TextView = view.findViewById(R.id.text_address)
        var textWorkingHours: TextView = view.findViewById(R.id.text_working_hours)
        var textCoordinate: TextView = view.findViewById(R.id.text_coordinate)
        var update: ImageView = view.findViewById(R.id.update)
        var delete: ImageView = view.findViewById(R.id.delete)
        private var listener: IRecyclerItemClickListener? = null
        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            listener!!.onItemClick(p0!!, adapterPosition)
        }
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
        holder.update.setOnClickListener {
            Common.pizzeriaSelected = items[position]
            EventBus.getDefault().postSticky(UpdatePizzeriaClick(true))
        }
        holder.setListener(object: IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int){
                Common.pizzeriaSelected = items[pos]
                EventBus.getDefault().postSticky(PizzeriaClick(true))
            }
        })
        holder.delete.setOnClickListener {
            val builder =
                androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
            builder.setTitle("Видалити піцерію").setMessage("Ви дійсно хочете видалити піцерію?")
                .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Так") { dialogInterface, _ ->
                    FirebaseDatabase.getInstance().getReference(Common.PIZZERIA_REF)
                        .child(items[position].id).removeValue()
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            items =
                                items.filterIndexed { index, _ -> index != position }
                            notifyItemRemoved(position)
                        }
                }
            val dialog = builder.create()
            dialog.show()
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.red))
            val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}
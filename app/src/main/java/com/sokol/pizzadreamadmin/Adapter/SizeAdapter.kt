package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.EventBus.SelectSizeModel
import com.sokol.pizzadreamadmin.EventBus.UpdateSize
import com.sokol.pizzadreamadmin.Model.SizeModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class SizeAdapter(var items: MutableList<SizeModel>, val context: Context) :
    RecyclerView.Adapter<SizeAdapter.MyViewHolder>() {
    var editPos:Int = -1
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var sizeName: TextView = view.findViewById(R.id.size_name)
        var sizePrice: TextView = view.findViewById(R.id.size_price)
        var btnSizeDelete: ImageView = view.findViewById(R.id.size_delete)
        private var listener: IRecyclerItemClickListener? = null
        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener!!.onItemClick(p0!!, adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_size_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val size = items[position]
        holder.sizeName.text = size.name
        holder.sizePrice.text = size.price.toString()
        holder.btnSizeDelete.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
            EventBus.getDefault().postSticky(UpdateSize(items))
        }
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                editPos = position
                EventBus.getDefault().postSticky(SelectSizeModel(items[pos]))
            }
        })
    }
    fun editSize(sizeModel: SizeModel) {
        items[editPos] = sizeModel
        notifyItemChanged(editPos)
        EventBus.getDefault().postSticky(UpdateSize(items))
    }
    override fun getItemCount(): Int {
        return items.size
    }

    fun addSize(sizeModel: SizeModel) {
        items.add(sizeModel)
        notifyItemInserted(items.size - 1)
        EventBus.getDefault().postSticky(UpdateSize(items))
    }

}
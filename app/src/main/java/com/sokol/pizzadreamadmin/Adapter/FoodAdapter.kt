package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.FoodItemClick
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class FoodAdapter(val items: List<FoodModel>, val context: Context) :
    RecyclerView.Adapter<FoodAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var foodName: TextView = view.findViewById(R.id.food_name)
        var foodImage: ImageView = view.findViewById(R.id.food_img)
        var foodDesc: TextView = view.findViewById(R.id.food_desc)
        var radioGroupSize: RadioGroup = view.findViewById(R.id.radio_group_size)
        var foodPrice: TextView = view.findViewById(R.id.food_price)
        var ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        var rating: TextView = view.findViewById(R.id.rating)
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
            LayoutInflater.from(parent.context).inflate(R.layout.layout_product_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(items[position].image).into(holder.foodImage)
        holder.foodName.text = items[position].name
        holder.foodDesc.text =
            Html.fromHtml(items[position].description, Html.FROM_HTML_MODE_LEGACY)
        holder.radioGroupSize.removeAllViews()
        val ratingAverage = items[position].ratingSum.toFloat() / items[position].ratingCount
        holder.ratingBar.rating = ratingAverage
        holder.rating.text =
            if (items[position].ratingCount == 0L) "0" else String.format("%.1f", ratingAverage)
        for (sizeModel in items[position].size) {
            val radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    items[position].userSelectedSize = sizeModel
                }
                val totalPrice = items[position].userSelectedSize?.price?.toDouble()
                val displayPrice = Math.round(totalPrice!! * 100.0) / 100.0
                holder.foodPrice.text =
                    StringBuilder("").append(Common.formatPrice(displayPrice)).toString()
            }
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            radioButton.layoutParams = params
            radioButton.text = sizeModel.name
            radioButton.tag = sizeModel.price
            holder.radioGroupSize.addView(radioButton)
        }
        if (holder.radioGroupSize.childCount > 0) {
            val radioButton = holder.radioGroupSize.getChildAt(0) as RadioButton
            radioButton.isChecked = true
        }
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = items[pos]
                EventBus.getDefault().postSticky(FoodItemClick(true))
            }
        })
    }
}
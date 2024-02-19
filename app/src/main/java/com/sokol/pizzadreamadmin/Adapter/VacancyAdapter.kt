package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.UpdateCategoryClick
import com.sokol.pizzadreamadmin.EventBus.UpdateVacancyClick
import com.sokol.pizzadreamadmin.EventBus.VacancyClick
import com.sokol.pizzadreamadmin.EventBus.VacancyItemClick
import com.sokol.pizzadreamadmin.Model.VacancyModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class VacancyAdapter(val items: List<VacancyModel>, val context: Context) :
    RecyclerView.Adapter<VacancyAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var image: ImageView = view.findViewById(R.id.vacancy_image)
        var name: TextView = view.findViewById(R.id.vacancy_name)
        var shortDesc: TextView = view.findViewById(R.id.vacancy_short_desc)
        var btnDetails: Button = view.findViewById(R.id.btn_vacancy_details)
        var update: ImageView = view.findViewById(R.id.update)
        private var listener: IRecyclerItemClickListener? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
        }

        override fun onClick(view: View) {
            listener?.onItemClick(view, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_vacancy_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val vacancyItem = items[position]
        Glide.with(context).load(vacancyItem.image).into(holder.image)
        holder.name.text = vacancyItem.name
        holder.shortDesc.text = Html.fromHtml(vacancyItem.shortDesc, Html.FROM_HTML_MODE_LEGACY)
        holder.update.setOnClickListener {
            Common.vacancySelected = items[position]
            EventBus.getDefault().postSticky(UpdateVacancyClick(true))
        }
        holder.btnDetails.setOnClickListener {
            Common.vacancySelected = vacancyItem
            EventBus.getDefault().postSticky(VacancyClick(true))
        }
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.vacancySelected = vacancyItem
                EventBus.getDefault().postSticky(VacancyItemClick(true))
            }

        })
    }
}
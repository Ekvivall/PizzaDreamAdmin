package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.UpdateCategoryClick
import com.sokol.pizzadreamadmin.EventBus.UpdateVacancyClick
import com.sokol.pizzadreamadmin.EventBus.VacancyClick
import com.sokol.pizzadreamadmin.EventBus.VacancyItemClick
import com.sokol.pizzadreamadmin.Model.VacancyModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class VacancyAdapter(var items: List<VacancyModel>, val context: Context) :
    RecyclerView.Adapter<VacancyAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var image: ImageView = view.findViewById(R.id.vacancy_image)
        var name: TextView = view.findViewById(R.id.vacancy_name)
        var shortDesc: TextView = view.findViewById(R.id.vacancy_short_desc)
        var btnDetails: Button = view.findViewById(R.id.btn_vacancy_details)
        var update: ImageView = view.findViewById(R.id.update)
        var delete: ImageView = view.findViewById(R.id.delete)
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
        holder.delete.setOnClickListener {
            val builder =
                androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
            builder.setTitle("Видалити вакансію").setMessage("Ви дійсно хочете видалити вакансію?")
                .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Так") { dialogInterface, _ ->
                    FirebaseDatabase.getInstance().getReference(Common.VACANCIES_REF)
                        .child(items[position].id).removeValue()
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            val storageReference = FirebaseStorage.getInstance().reference
                            val fileReference  =
                                storageReference.child("vacancies/"+items[position].id)
                            fileReference.delete()
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
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.vacancySelected = vacancyItem
                EventBus.getDefault().postSticky(VacancyItemClick(true))
            }

        })
    }
}
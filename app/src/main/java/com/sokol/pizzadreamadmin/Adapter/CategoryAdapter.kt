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
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.CategoryClick
import com.sokol.pizzadreamadmin.EventBus.UpdateCategoryClick
import com.sokol.pizzadreamadmin.Model.CategoryModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class CategoryAdapter(var items: List<CategoryModel>, val context: Context) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var categoryName: TextView = view.findViewById(R.id.category_name)
        var categoryImage: ImageView = view.findViewById(R.id.category_image)
        var update: ImageView = view.findViewById(R.id.update)
        var delete: ImageView = view.findViewById(R.id.delete)
        private var listener:IRecyclerItemClickListener? = null
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
            .inflate(R.layout.layout_category_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(items[position].image).into(holder.categoryImage)
        holder.categoryName.text = items[position].name
        holder.update.setOnClickListener {
            Common.categorySelected = items[position]
            EventBus.getDefault().postSticky(UpdateCategoryClick(true))
        }
        holder.delete.setOnClickListener {
            val builder =
                androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
            builder.setTitle("Видалити страву").setMessage("Ви дійсно хочете видалити страву?")
                .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Так") { dialogInterface, _ ->
                    FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                        .child(items[position].id.toString()).removeValue()
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            val storageReference = FirebaseStorage.getInstance().reference
                            val fileReference  =
                                storageReference.child("icon_category/"+items[position].id)
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
        holder.setListener(object: IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int){
                Common.categorySelected = items[pos]
                EventBus.getDefault().postSticky(CategoryClick(true))
            }
        })
    }

}
package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.content.DialogInterface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.CommentModel
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.R

class CommentAdapter(var items: List<CommentModel>, val context: Context) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var commentImage: ImageView = view.findViewById(R.id.comment_image)
        var commentName: TextView = view.findViewById(R.id.comment_name)
        var commentDate: TextView = view.findViewById(R.id.comment_date)
        var commentText: TextView = view.findViewById(R.id.comment_text)
        var ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        var btnCommentDelete: ImageView = view.findViewById(R.id.comment_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_comment_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val comment = items[position]
        if (comment.avatar.isNotEmpty()) {
            Glide.with(context).load(comment.avatar).into(holder.commentImage)
        }
        holder.commentName.text = comment.name
        holder.commentDate.text = DateUtils.getRelativeTimeSpanString(comment.commentTimeStamp)
        holder.commentText.text = comment.comment
        holder.ratingBar.rating = comment.ratingValue.toFloat()
        holder.btnCommentDelete.setOnClickListener {
            val builder =
                androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
            builder.setTitle("Видалити відгук").setMessage("Ви дійсно хочете видалити відгук?")
                .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Так") { dialogInterface, _ ->
                    FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
                        .child(Common.foodSelected?.id.toString()).child(comment.id).removeValue()
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                .child(comment.categoryId).child("foods").child(comment.foodId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val foodModel = snapshot.getValue(FoodModel::class.java)
                                        val ratingSum = foodModel?.ratingSum!! - comment.ratingValue
                                        val ratingCount = foodModel.ratingCount - 1
                                        val updateDataRating = HashMap<String, Any>()
                                        updateDataRating["ratingSum"] = ratingSum
                                        updateDataRating["ratingCount"] = ratingCount
                                        foodModel.ratingCount = ratingCount
                                        foodModel.ratingSum = ratingSum
                                        Common.foodSelected = foodModel
                                        Common.categorySelected?.foods?.put(foodModel.id!!, foodModel)
                                        snapshot.ref.updateChildren(updateDataRating)
                                            .addOnCompleteListener {
                                                items =
                                                    items.filterIndexed { index, _ -> index != position }
                                                notifyDataSetChanged()
                                            }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                                    }

                                })
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

    override fun getItemCount(): Int {
        return items.size
    }

}
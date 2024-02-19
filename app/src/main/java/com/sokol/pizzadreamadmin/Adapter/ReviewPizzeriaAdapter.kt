package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Model.ReviewModel
import com.sokol.pizzadreamadmin.R

class ReviewPizzeriaAdapter(val items: List<ReviewModel>, val context: Context) :
    RecyclerView.Adapter<ReviewPizzeriaAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var commentName: TextView = view.findViewById(R.id.comment_name)
        var commentDate: TextView = view.findViewById(R.id.comment_date)
        var commentText: TextView = view.findViewById(R.id.comment_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_review_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val comment = items[position]
        holder.commentName.text = comment.name
        holder.commentDate.text = DateUtils.getRelativeTimeSpanString(comment.commentTimeStamp)
        holder.commentText.text = comment.comment
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
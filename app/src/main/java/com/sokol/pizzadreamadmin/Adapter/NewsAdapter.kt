package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.content.DialogInterface
import android.text.Html
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
import com.sokol.pizzadreamadmin.EventBus.NewsItemClick
import com.sokol.pizzadreamadmin.EventBus.UpdateCategoryClick
import com.sokol.pizzadreamadmin.EventBus.UpdateNewsClick
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date

class NewsAdapter(var items: List<NewsModel>, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {
    private var simpleDateFormat= SimpleDateFormat("dd MMM yyyy")
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var image: ImageView = view.findViewById(R.id.news_image)
        var title: TextView = view.findViewById(R.id.news_title)
        var content: TextView = view.findViewById(R.id.news_content)
        var date: TextView = view.findViewById(R.id.news_date)
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
            LayoutInflater.from(parent.context).inflate(R.layout.layout_news_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val newsItem = items[position]
        Glide.with(context).load(newsItem.image).into(holder.image)
        holder.title.text = newsItem.title
        holder.content.text = Html.fromHtml(newsItem.content, Html.FROM_HTML_MODE_LEGACY)
        val date = Date(newsItem.date)
        holder.date.text = StringBuilder(simpleDateFormat.format(date))
        holder.update.setOnClickListener {
            Common.newsSelected = items[position]
            EventBus.getDefault().postSticky(UpdateNewsClick(true))
        }
        holder.delete.setOnClickListener {
            val builder =
                androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
            builder.setTitle("Видалити новину").setMessage("Ви дійсно хочете видалити новину?")
                .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Так") { dialogInterface, _ ->
                    FirebaseDatabase.getInstance().getReference(Common.NEWS_REF)
                        .child(items[position].id.toString()).removeValue()
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            val storageReference = FirebaseStorage.getInstance().reference
                            val fileReference  =
                                storageReference.child("news/"+items[position].id)
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
                Common.newsSelected = items[pos]
                EventBus.getDefault().postSticky(NewsItemClick(true))
            }

        })
    }
}
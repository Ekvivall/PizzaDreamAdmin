package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Model.ResumeModel
import com.sokol.pizzadreamadmin.R
import java.text.SimpleDateFormat
import java.util.Date

class ResumeAdapter (val items: List<ResumeModel>, val context: Context) :
    RecyclerView.Adapter<ResumeAdapter.MyViewHolder>() {
    private var simpleDateFormat= SimpleDateFormat("dd MMM yyyy hh:mm")

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var resumeName: TextView = view.findViewById(R.id.resume_name)
        var resumeDateOfBirth: TextView = view.findViewById(R.id.resume_date_of_birth)
        var resumePhone: TextView = view.findViewById(R.id.resume_phone)
        var resumeEmail: TextView = view.findViewById(R.id.resume_email)
        var btnResumeFile: Button = view.findViewById(R.id.btn_resume_file)
        var resumeTimeStamp: TextView = view.findViewById(R.id.resume_time_stamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_resume_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val resume = items[position]
        holder.resumeName.text = StringBuilder(resume.surname).append(" ").append(resume.name)
        holder.resumeDateOfBirth.text = resume.dateOfBirth
        holder.resumePhone.text = resume.phone
        holder.resumeEmail.text = resume.email
        val date = Date(resume.resumeTimeStamp)
        holder.resumeTimeStamp.text = StringBuilder(simpleDateFormat.format(date))
        holder.btnResumeFile.setOnClickListener {
            val resumeUrl = resume.resumeFile
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resumeUrl))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
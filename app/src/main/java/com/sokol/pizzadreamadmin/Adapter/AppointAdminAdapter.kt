package com.sokol.pizzadreamadmin.Adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.ProfileClick
import com.sokol.pizzadreamadmin.MainActivity
import com.sokol.pizzadreamadmin.R
import com.sokol.pizzadreamadmin.Model.UserModel
import org.greenrobot.eventbus.EventBus

class AppointAdminAdapter(var items: List<UserModel>, val context: Context) :
    RecyclerView.Adapter<AppointAdminAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.profile_name)
        var email: TextView = view.findViewById(R.id.profile_email)
        var btnAppointAdmin: Button = view.findViewById(R.id.btn_appoint_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_appoint_admin, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = items[position]
        holder.name.text = StringBuilder(user.lastName).append(" ").append(user.firstName)
        holder.email.text = user.email
        holder.btnAppointAdmin.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
            builder.setTitle("Призначення адміністратором").setMessage("Ви дійсно хочете призначити адміністратором?")
                .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Так") { dialogInterface, _ ->
                    val updateData = HashMap<String, Any>()
                    updateData["role"] = "admin"
                    FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
                        .child(user.uid).updateChildren(updateData)
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }.addOnSuccessListener {
                            items = items.filterIndexed { index, _ -> index != position }
                            notifyDataSetChanged()
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
package com.sokol.pizzadreamadmin.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.sokol.pizzadreamadmin.Model.FCMSendData
import com.sokol.pizzadreamadmin.Callback.IRecyclerItemClickListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.OrderDetailClick
import com.sokol.pizzadreamadmin.Model.OrderModel
import com.sokol.pizzadreamadmin.Model.TokenModel
import com.sokol.pizzadreamadmin.R
import com.sokol.pizzadreamadmin.Remote.IFCMService
import com.sokol.pizzadreamadmin.Remote.RetrofitFCMClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date

class OrderAdapter(val items: List<OrderModel>, val context: Context) :
    RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {
    private var simpleDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm")
    private val compositeDisposable = CompositeDisposable()
    private val ifcmService = RetrofitFCMClient.getInstance().create(IFCMService::class.java)

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var status: TextView = view.findViewById(R.id.order_status)
        var id: TextView = view.findViewById(R.id.order_id)
        var date: TextView = view.findViewById(R.id.order_date)
        var address: TextView = view.findViewById(R.id.customer_address)
        var phone: TextView = view.findViewById(R.id.customer_phone)
        var totalPrice: TextView = view.findViewById(R.id.total_price)
        var recyclerView: RecyclerView = view.findViewById(R.id.order_foods_recycler)
        var delivery: TextView = view.findViewById(R.id.delivery)
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
            LayoutInflater.from(parent.context).inflate(R.layout.layout_order_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderItem = items[position]
        holder.recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        holder.recyclerView.layoutManager = layoutManager
        val adapter = OrderFoodAdapter(
            orderItem.cartItems!!, context, orderItem
        )
        if (orderItem.status == Common.STATUSES[4]) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        if (orderItem.status == Common.STATUSES[4] || orderItem.status == Common.STATUSES[5]) {
            holder.update.visibility = View.GONE
        } else {
            holder.update.visibility = View.VISIBLE
        }
        holder.recyclerView.adapter = adapter
        val date = Date(orderItem.orderedTime)
        holder.date.text = StringBuilder(simpleDateFormat.format(date))
        holder.phone.text = orderItem.customerPhone
        holder.phone.setOnClickListener {
            Dexter.withContext(context).withPermission(android.Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_DIAL
                        intent.data = Uri.parse(
                            StringBuilder("tel:").append(orderItem.customerPhone).toString()
                        )
                        context.startActivity(intent)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            context,
                            "Ви повинні прийняти цей дозвіл" + p0!!.permissionName,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?, p1: PermissionToken?
                    ) {
                    }

                }).check()
        }
        holder.id.text = StringBuilder("№ ").append(orderItem.orderId)
        holder.status.text = StringBuilder("Статус: ").append(orderItem.status)
        holder.totalPrice.text =
            StringBuilder("Всього: ").append(Common.formatPrice(orderItem.totalPrice))
        if (orderItem.isDeliveryAddress) {
            holder.delivery.visibility = View.VISIBLE
            holder.address.text = StringBuilder(orderItem.customerAddress.toString())
        } else {
            holder.delivery.visibility = View.GONE
            holder.address.text = StringBuilder("Pizza Dream: ").append(orderItem.customerAddress)
        }
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.orderSelected = items[pos]
                EventBus.getDefault().postSticky(OrderDetailClick(true))
            }

        })
        holder.update.setOnClickListener {
            val layoutDialog =
                LayoutInflater.from(context).inflate(R.layout.layout_status_update, null)
            val builder = AlertDialog.Builder(context).setView(layoutDialog)
            val spnStatus = layoutDialog.findViewById<Spinner>(R.id.spn_order_status)
            val btnOk = layoutDialog.findViewById<Button>(R.id.btn_ok)
            val btnCancel = layoutDialog.findViewById<Button>(R.id.btn_cancel)
            val adapterStatus = ArrayAdapter(
                context, android.R.layout.simple_spinner_dropdown_item, Common.STATUSES
            )
            adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnStatus.adapter = adapterStatus
            spnStatus.setSelection(Common.STATUSES.indexOf(orderItem.status))
            val dialog = builder.create()
            dialog.show()
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnOk.setOnClickListener {
                val updateData = HashMap<String, Any>()
                updateData["status"] = spnStatus.selectedItem
                FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                    .child(orderItem.orderId!!).updateChildren(updateData)
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context, e.message, Toast.LENGTH_SHORT
                        ).show()
                    }.addOnSuccessListener {
                        orderItem.status =
                            spnStatus.selectedItem.toString()
                        notifyItemChanged(position)
                        FirebaseDatabase.getInstance()
                            .getReference(Common.TOKEN_REF)
                            .child(orderItem.userId!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val tokenModel = snapshot.getValue<TokenModel>()
                                        val dataSend = HashMap<String, String>()
                                        dataSend[Common.NOTIFICATION_TITLE] =
                                            "Ваше замовлення оновлено"
                                        dataSend[Common.NOTIFICATION_CONTENT] =
                                            "Ваше замовлення ${orderItem.status}"
                                        val sendData =
                                            FCMSendData(tokenModel!!.token, dataSend)
                                        compositeDisposable.add(
                                            ifcmService.sendNotification(sendData)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe ())
                                    }
                                    //
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        context, error.message, Toast.LENGTH_SHORT
                                    ).show()
                                }

                            })
                    }
                dialog.dismiss()
            }
        }
    }

    fun onStop() {
        compositeDisposable.clear()
    }
}
package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.OrderModel

interface IOrderLoadCallback {
    fun onOrderLoadSuccess(orderList: List<OrderModel>)
    fun onOrderLoadFailed(message:String)
}
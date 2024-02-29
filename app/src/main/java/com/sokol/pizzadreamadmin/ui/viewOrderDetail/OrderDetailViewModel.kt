package com.sokol.pizzadreamadmin.ui.viewOrderDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.OrderModel

class OrderDetailViewModel:ViewModel() {
    private var orderMutableLiveData: MutableLiveData<OrderModel>? = null
    fun getOrderDetailMutableLiveData(): MutableLiveData<OrderModel> {
        if (orderMutableLiveData == null) {
            orderMutableLiveData = MutableLiveData()
        }
        orderMutableLiveData!!.value = Common.orderSelected
        return orderMutableLiveData!!
    }
}
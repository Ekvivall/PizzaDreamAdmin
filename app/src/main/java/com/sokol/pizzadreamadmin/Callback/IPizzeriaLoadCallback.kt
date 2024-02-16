package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.PizzeriaModel

interface IPizzeriaLoadCallback {
    fun onPizzeriaLoadSuccess(addressList: List<PizzeriaModel>)
    fun onPizzeriaLoadFailed(message:String)
}
package com.sokol.pizzadreamadmin.ui.updatePizzeria

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.PizzeriaModel

class UpdatePizzeriaViewModel: ViewModel() {
    private var pizzeriaMutableLiveData: MutableLiveData<PizzeriaModel>? = null
    fun getPizzeriaMutableLiveData(): MutableLiveData<PizzeriaModel> {
        if (pizzeriaMutableLiveData == null) {
            pizzeriaMutableLiveData = MutableLiveData()
        }
        pizzeriaMutableLiveData!!.value = Common.pizzeriaSelected
        return pizzeriaMutableLiveData!!
    }
}
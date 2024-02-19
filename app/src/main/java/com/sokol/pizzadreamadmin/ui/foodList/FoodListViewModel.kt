package com.sokol.pizzadreamadmin.ui.foodList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.FoodModel

class FoodListViewModel: ViewModel() {
    private var foodListMutableLiveData: MutableLiveData<List<FoodModel>>? = null
    fun getFoodListMutableLiveData(): MutableLiveData<List<FoodModel>> {
        if (foodListMutableLiveData == null) {
            foodListMutableLiveData = MutableLiveData()
        }
        val foodList = Common.categorySelected!!.foods?.values?.toList() ?: emptyList()
        foodListMutableLiveData!!.value = foodList
        return foodListMutableLiveData!!
    }
}
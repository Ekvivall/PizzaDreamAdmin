package com.sokol.pizzadreamadmin.ui.updateFood

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Callback.IAddonCategoryLoadCallback
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.AddonCategoryModel
import com.sokol.pizzadreamadmin.Model.FoodModel

class UpdateFoodViewModel: ViewModel() {
    private var foodDetailMutableLiveData: MutableLiveData<FoodModel>? = null
    fun getFoodDetailMutableLiveData(): MutableLiveData<FoodModel> {
        if (foodDetailMutableLiveData == null) {
            foodDetailMutableLiveData = MutableLiveData()
        }
        foodDetailMutableLiveData!!.value = Common.foodSelected
        return foodDetailMutableLiveData!!
    }
}
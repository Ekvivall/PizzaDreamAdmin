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
        val foodList = mutableListOf<FoodModel>()
        val foodsWithImage = mutableListOf<FoodModel>()
        Common.categorySelected?.foods?.values?.forEach { foodModel ->
            if (foodModel.image.isNullOrEmpty()) {
                foodList.add(0, foodModel) // додавання на початок списку
            } else {
                foodsWithImage.add(foodModel)
            }
        }
        // Сортування списку піц з зображенням за назвою
        foodsWithImage.sortBy { it.name }
        foodList.addAll(foodsWithImage)
        foodListMutableLiveData!!.value = foodList
        return foodListMutableLiveData!!
    }
}
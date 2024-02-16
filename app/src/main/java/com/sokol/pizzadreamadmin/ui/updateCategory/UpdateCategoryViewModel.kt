package com.sokol.pizzadreamadmin.ui.updateCategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.CategoryModel

class UpdateCategoryViewModel : ViewModel(){
    private var categoryMutableLiveData: MutableLiveData<CategoryModel>? = null
    fun getCategoryMutableLiveData(): MutableLiveData<CategoryModel> {
        if (categoryMutableLiveData == null) {
            categoryMutableLiveData = MutableLiveData()
        }
        categoryMutableLiveData!!.value = Common.categorySelected
        return categoryMutableLiveData!!
    }
}
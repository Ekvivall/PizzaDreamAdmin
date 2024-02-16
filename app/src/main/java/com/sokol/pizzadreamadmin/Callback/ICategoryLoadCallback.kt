package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.CategoryModel

interface ICategoryLoadCallback {
    fun onCategoryLoadSuccess(categoriesList: List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}
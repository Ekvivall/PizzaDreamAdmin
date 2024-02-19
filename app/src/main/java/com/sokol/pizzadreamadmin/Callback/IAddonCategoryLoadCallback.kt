package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.AddonCategoryModel

interface IAddonCategoryLoadCallback {
    fun onCategoryLoadSuccess(categoriesList: List<AddonCategoryModel>)
    fun onCategoryLoadFailed(message:String)
}
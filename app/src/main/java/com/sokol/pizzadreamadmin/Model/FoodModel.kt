package com.sokol.pizzadreamadmin.Model

import com.sokol.pizzadream.Model.AddonModel
import com.sokol.pizzadream.Model.SizeModel

class FoodModel {
    var id:String? = null
    var categoryId:String? = null
    var name: String? = null
    var image: String? = null
    var description: String? = null
    var addon: String? = null
    var size: List<SizeModel> = ArrayList()
    var ratingSum: Long = 0L
    var ratingCount: Long = 0L
    var userSelectedAddon:MutableList<AddonModel>?=null
    var userSelectedSize:SizeModel?=null
}
package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.NewsModel

interface INewsLoadCallback {
    fun onNewsLoadSuccess(newsList: List<NewsModel>)
    fun onNewsLoadFailed(message:String)
}
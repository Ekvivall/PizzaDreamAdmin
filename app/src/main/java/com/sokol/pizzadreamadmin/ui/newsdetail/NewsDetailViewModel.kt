package com.sokol.pizzadreamadmin.ui.newsdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.NewsModel

class NewsDetailViewModel : ViewModel() {

    private var newsDetailMutableLiveData: MutableLiveData<NewsModel>? = null
    fun getNewsDetailMutableLiveData(): MutableLiveData<NewsModel> {
        if (newsDetailMutableLiveData == null) {
            newsDetailMutableLiveData = MutableLiveData()
        }
        newsDetailMutableLiveData!!.value = Common.newsSelected
        return newsDetailMutableLiveData!!
    }

}
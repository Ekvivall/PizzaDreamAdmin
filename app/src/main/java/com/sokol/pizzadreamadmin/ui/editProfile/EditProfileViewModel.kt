package com.sokol.pizzadreamadmin.ui.editProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.UserModel

class EditProfileViewModel : ViewModel() {
    private var userMutableLiveData: MutableLiveData<UserModel>? = null
    fun getUserMutableLiveData(): MutableLiveData<UserModel> {
        if (userMutableLiveData == null) {
            userMutableLiveData = MutableLiveData()
        }
        userMutableLiveData!!.value = Common.currentUser
        return userMutableLiveData!!
    }

}
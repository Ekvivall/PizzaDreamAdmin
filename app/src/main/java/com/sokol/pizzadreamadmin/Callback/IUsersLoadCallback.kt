package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.UserModel

interface IUsersLoadCallback {
    fun onUsersLoadSuccess(usersList: List<UserModel>)
    fun onUsersLoadFailed(message:String)
}
package com.sokol.pizzadreamadmin.ui.appointAdmin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokol.pizzadreamadmin.Callback.IUsersLoadCallback
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.UserModel

class AppointAdminViewModel : ViewModel(), IUsersLoadCallback {
    private var usersMutableLiveData: MutableLiveData<List<UserModel>>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var usersLoadCallbackListener: IUsersLoadCallback = this
    val users: LiveData<List<UserModel>>
        get() {
            if (usersMutableLiveData == null) {
                usersMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadUsers()
            }
            return usersMutableLiveData!!
        }

    private fun loadUsers() {
        val tempList = ArrayList<UserModel>()
        val usersRef =
            FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).orderByChild("role")
                .equalTo("")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemsSnapshot in snapshot.children) {
                    val model = itemsSnapshot.getValue(UserModel::class.java)
                    tempList.add(model!!)
                }
                usersLoadCallbackListener.onUsersLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                usersLoadCallbackListener.onUsersLoadFailed(error.message)
            }

        })
    }

    override fun onUsersLoadSuccess(usersList: List<UserModel>) {
        usersMutableLiveData?.value = usersList
    }

    override fun onUsersLoadFailed(message: String) {
        messageError.value = message
    }
}
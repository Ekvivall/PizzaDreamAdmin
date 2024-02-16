package com.sokol.pizzadreamadmin.ui.pizzerias

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokol.pizzadreamadmin.Callback.IPizzeriaLoadCallback
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.PizzeriaModel

class PizzeriasViewModel : ViewModel(), IPizzeriaLoadCallback {

    private var pizzeriasMutableLiveData: MutableLiveData<List<PizzeriaModel>>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var pizzeriaLoadCallbackListener: IPizzeriaLoadCallback = this
    fun getPizzeriasMutableLiveData(): MutableLiveData<List<PizzeriaModel>> {
        if (pizzeriasMutableLiveData == null) {
            pizzeriasMutableLiveData = MutableLiveData()
        }
        loadPizzerias()
        return pizzeriasMutableLiveData!!
    }

    private fun loadPizzerias() {
        val tempList = ArrayList<PizzeriaModel>()
        val addressRef = FirebaseDatabase.getInstance().getReference(Common.PIZZERIA_REF)
        addressRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val model = itemSnapshot.getValue(PizzeriaModel::class.java)
                    tempList.add(model!!)
                }
                pizzeriaLoadCallbackListener.onPizzeriaLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                pizzeriaLoadCallbackListener.onPizzeriaLoadFailed(error.message)
            }

        })
    }

    override fun onPizzeriaLoadSuccess(addressList: List<PizzeriaModel>) {
        pizzeriasMutableLiveData?.value = addressList
    }

    override fun onPizzeriaLoadFailed(message: String) {
        messageError.value = message
    }
}
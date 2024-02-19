package com.sokol.pizzadreamadmin.ui.resumes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokol.pizzadreamadmin.Callback.IResumesPizzeriaLoadCallback
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.ResumeModel

class ResumesViewModel: ViewModel(), IResumesPizzeriaLoadCallback {
    private var resumesMutableLiveData: MutableLiveData<List<ResumeModel>>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var resumesLoadCallbackListener: IResumesPizzeriaLoadCallback = this
    val resumes: LiveData<List<ResumeModel>>
        get() {
            if (resumesMutableLiveData == null) {
                resumesMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadResumes()
            }
            return resumesMutableLiveData!!
        }

    private fun loadResumes() {
        val tempList = ArrayList<ResumeModel>()
        val newsRef = FirebaseDatabase.getInstance().getReference(Common.RESUMES_REF)
            .child(Common.vacancySelected?.id.toString())
        newsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemsSnapshot in snapshot.children) {
                    val model = itemsSnapshot.getValue(ResumeModel::class.java)
                    tempList.add(model!!)
                }
                val sortedList = tempList.sortedByDescending { it.resumeTimeStamp }
                resumesLoadCallbackListener.onResumeLoadSuccess(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                resumesLoadCallbackListener.onResumeLoadFailed(error.message)
            }

        })
    }

    override fun onResumeLoadSuccess(resumesList: List<ResumeModel>) {
        resumesMutableLiveData?.value = resumesList
    }

    override fun onResumeLoadFailed(message: String) {
        messageError.value = message
    }
}
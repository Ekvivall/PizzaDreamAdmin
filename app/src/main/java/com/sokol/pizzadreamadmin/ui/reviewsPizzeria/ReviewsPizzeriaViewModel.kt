package com.sokol.pizzadreamadmin.ui.reviewsPizzeria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokol.pizzadreamadmin.Callback.IReviewsPizzeriaLoadCallback
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.ReviewModel

class ReviewsPizzeriaViewModel: ViewModel(), IReviewsPizzeriaLoadCallback {
    private var commentListMutableLiveData: MutableLiveData<List<ReviewModel>>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var commentLoadCallbackListener: IReviewsPizzeriaLoadCallback = this
    val comments: LiveData<List<ReviewModel>>
        get() {
            if (commentListMutableLiveData == null) {
                commentListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadCommentList()
            }
            return commentListMutableLiveData!!
        }

    private fun loadCommentList() {
        val tempList = ArrayList<ReviewModel>()
        val newsRef = FirebaseDatabase.getInstance().getReference(Common.REVIEWS_REF)
            .child(Common.pizzeriaSelected?.id.toString())
        newsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemsSnapshot in snapshot.children) {
                    val model = itemsSnapshot.getValue(ReviewModel::class.java)
                    tempList.add(model!!)
                }
                val sortedList = tempList.sortedByDescending { it.commentTimeStamp }
                commentLoadCallbackListener.onCommentLoadSuccess(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                commentLoadCallbackListener.onCommentLoadFailed(error.message)
            }

        })
    }

    override fun onCommentLoadSuccess(commentsList: List<ReviewModel>) {
        commentListMutableLiveData?.value = commentsList
    }

    override fun onCommentLoadFailed(message: String) {
        messageError.value = message
    }
}
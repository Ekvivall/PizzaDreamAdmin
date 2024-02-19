package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.ReviewModel

interface IReviewsPizzeriaLoadCallback {
    fun onCommentLoadSuccess(commentsList: List<ReviewModel>)
    fun onCommentLoadFailed(message:String)
}
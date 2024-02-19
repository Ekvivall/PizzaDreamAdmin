package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.CommentModel

interface ICommentLoadCallback {
    fun onCommentLoadSuccess(commentsList: List<CommentModel>)
    fun onCommentLoadFailed(message:String)
}
package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.ResumeModel

interface IResumesPizzeriaLoadCallback {
    fun onResumeLoadSuccess(resumesList: List<ResumeModel>)
    fun onResumeLoadFailed(message:String)

}

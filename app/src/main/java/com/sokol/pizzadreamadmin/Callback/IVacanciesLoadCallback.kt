package com.sokol.pizzadreamadmin.Callback

import com.sokol.pizzadreamadmin.Model.VacancyModel

interface IVacanciesLoadCallback {
    fun onVacanciesLoadSuccess(vacanciesList: List<VacancyModel>)
    fun onVacanciesLoadFailed(message: String)
}
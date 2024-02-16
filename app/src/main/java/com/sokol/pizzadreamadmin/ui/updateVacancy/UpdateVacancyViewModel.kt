package com.sokol.pizzadreamadmin.ui.updateVacancy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.VacancyModel

class UpdateVacancyViewModel: ViewModel() {
    private var vacancyMutableLiveData: MutableLiveData<VacancyModel>? = null
    fun getVacancyDetailMutableLiveData(): MutableLiveData<VacancyModel> {
        if (vacancyMutableLiveData == null) {
            vacancyMutableLiveData = MutableLiveData()
        }
        vacancyMutableLiveData!!.value = Common.vacancySelected
        return vacancyMutableLiveData!!
    }
}
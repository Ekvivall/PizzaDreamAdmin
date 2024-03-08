package com.sokol.pizzadreamadmin.ui.vacancies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Adapter.VacancyAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.AddVacancyClick
import com.sokol.pizzadreamadmin.EventBus.UpdatePizzeriaClick
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class VacanciesFragment : Fragment() {
    private lateinit var vacanciesRecycler: RecyclerView
    private lateinit var btnCreate: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val vacanciesViewModel = ViewModelProvider(this).get(VacanciesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_vacancies, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            vacanciesViewModel.vacancies.observe(viewLifecycleOwner) {
                val listData = it
                val vacancyAdapter = VacancyAdapter(listData, requireContext())
                vacanciesRecycler.adapter = vacancyAdapter
                val vacancyNames = Array(it.size) { i -> it[i].name }
                val adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item, vacancyNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun initView(root: View) {
        vacanciesRecycler = root.findViewById(R.id.vacancies_recycler)
        vacanciesRecycler.setHasFixedSize(true)
        vacanciesRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        btnCreate = root.findViewById(R.id.create)
        btnCreate.setOnClickListener {
            EventBus.getDefault().postSticky(AddVacancyClick(true))
        }
    }


}
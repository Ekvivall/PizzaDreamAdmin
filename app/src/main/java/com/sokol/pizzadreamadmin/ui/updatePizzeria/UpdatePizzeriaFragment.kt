package com.sokol.pizzadreamadmin.ui.updatePizzeria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.PizzeriasClick
import com.sokol.pizzadreamadmin.Model.PizzeriaModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class UpdatePizzeriaFragment : Fragment() {
    private lateinit var tilPizzeriaName: TextInputLayout
    private lateinit var edtPizzeriaName: EditText
    private lateinit var tilPizzeriaScheduleWork: TextInputLayout
    private lateinit var edtPizzeriaScheduleWork: EditText
    private lateinit var tilPizzeriaLat: TextInputLayout
    private lateinit var edtPizzeriaLat: EditText
    private lateinit var tilPizzeriaLng: TextInputLayout
    private lateinit var edtPizzeriaLng: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val updatePizzeriaViewModel = ViewModelProvider(this)[UpdatePizzeriaViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_update_pizzeria, container, false)
        initView(root)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (Common.isConnectedToInternet(requireContext())) {
            updatePizzeriaViewModel.getPizzeriaMutableLiveData().observe(viewLifecycleOwner) {
                if (it != null) {
                    displayInfo(it)
                    actionBar?.title = "Додавання піцерії"
                }
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: PizzeriaModel) {
        edtPizzeriaName.setText(it.name)
        edtPizzeriaScheduleWork.setText(it.scheduleWork)
        edtPizzeriaLat.setText(it.lat.toString())
        edtPizzeriaLng.setText(it.lng.toString())
    }

    private fun initView(root: View) {
        tilPizzeriaName = root.findViewById(R.id.til_pizzeria_name)
        edtPizzeriaName = root.findViewById(R.id.edt_pizzeria_name)
        tilPizzeriaScheduleWork = root.findViewById(R.id.til_pizzeria_schedule_work)
        edtPizzeriaScheduleWork = root.findViewById(R.id.edt_pizzeria_schedule_work)
        tilPizzeriaLat = root.findViewById(R.id.til_pizzeria_lat)
        edtPizzeriaLat = root.findViewById(R.id.edt_pizzeria_lat)
        tilPizzeriaLng = root.findViewById(R.id.til_pizzeria_lng)
        edtPizzeriaLng = root.findViewById(R.id.edt_pizzeria_lng)
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                val name = edtPizzeriaName.text.toString().trim()
                tilPizzeriaName.error = null
                val scheduleWork = edtPizzeriaScheduleWork.text.toString().trim()
                tilPizzeriaScheduleWork.error = null
                val lat = edtPizzeriaLat.text.toString().trim()
                tilPizzeriaLat.error = null
                val lng = edtPizzeriaLng.text.toString().trim()
                tilPizzeriaLng.error = null
                if (name.isEmpty()) {
                    tilPizzeriaName.error = "Будь ласка, введіть адресу піцерії"
                    return@setOnClickListener
                }
                if (scheduleWork.isEmpty()) {
                    tilPizzeriaScheduleWork.error = "Будь ласка, введіть час роботи піцерії"
                    return@setOnClickListener
                }
                if (lat.isEmpty()) {
                    tilPizzeriaLat.error = "Будь ласка, введіть широту"
                    return@setOnClickListener
                }
                if (lng.isEmpty()) {
                    tilPizzeriaLng.error = "Будь ласка, введіть довготу"
                    return@setOnClickListener
                }
                if (Common.pizzeriaSelected != null) {
                    val updateData = HashMap<String, Any>()
                    updateData["name"] = name
                    updateData["scheduleWork"] = scheduleWork
                    updateData["lat"] = lat.toDouble()
                    updateData["lng"] = lng.toDouble()
                    updatePizzeria(updateData)
                } else {
                    val pizzeriaModel = PizzeriaModel()
                    val pizzeriaRef =
                        FirebaseDatabase.getInstance().getReference(Common.PIZZERIA_REF)
                    pizzeriaModel.id = pizzeriaRef.push().key.toString()
                    pizzeriaModel.name = name
                    pizzeriaModel.scheduleWork = scheduleWork
                    pizzeriaModel.lat = lat.toDouble()
                    pizzeriaModel.lng = lng.toDouble()
                    addPizzeria(pizzeriaModel)
                }
            } else {
                Toast.makeText(
                    requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addPizzeria(pizzeriaModel: PizzeriaModel) {
        FirebaseDatabase.getInstance().getReference(Common.PIZZERIA_REF).child(pizzeriaModel.id)
            .setValue(pizzeriaModel)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно додано!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(PizzeriasClick(true))
            }
    }

    private fun updatePizzeria(updateData: HashMap<String, Any>) {
        FirebaseDatabase.getInstance().getReference(Common.PIZZERIA_REF)
            .child(Common.pizzeriaSelected!!.id).updateChildren(updateData)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно оновлено!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(PizzeriasClick(true))
            }
    }

}
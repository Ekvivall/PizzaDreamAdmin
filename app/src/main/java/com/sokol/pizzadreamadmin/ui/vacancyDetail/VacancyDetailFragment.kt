package com.sokol.pizzadreamadmin.ui.vacancyDetail

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.VacanciesClick
import com.sokol.pizzadreamadmin.Model.VacancyModel
import com.sokol.pizzadreamadmin.R
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Calendar

class VacancyDetailFragment : Fragment() {
    private lateinit var vacancyImage: ImageView
    private lateinit var vacancyName: TextView
    private lateinit var vacancyDesc: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val vacanciesViewModel = ViewModelProvider(this).get(VacancyDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_vacancy_detail, container, false)
        initView(root)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (Common.isConnectedToInternet(requireContext())) {
            vacanciesViewModel.getVacancyDetailMutableLiveData().observe(viewLifecycleOwner) {
                displayInfo(it)
                actionBar?.title = it.name
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: VacancyModel) {
        Glide.with(requireContext()).load(it.image).into(vacancyImage)
        vacancyName.text = it.name
        vacancyDesc.text = Html.fromHtml(it.desc, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun initView(root: View) {
        vacancyImage = root.findViewById(R.id.vacancy_image)
        vacancyName = root.findViewById(R.id.vacancy_name)
        vacancyDesc = root.findViewById(R.id.vacancy_desc)
    }
}
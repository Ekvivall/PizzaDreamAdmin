package com.sokol.pizzadreamadmin.ui.addVacancy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
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

class AddVacancyFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var tilVacancyName: TextInputLayout
    private lateinit var edtVacancyName: EditText
    private lateinit var tilVacancyShortDesc: TextInputLayout
    private lateinit var edtVacancyShortDesc: EditText
    private lateinit var tilVacancyDesc: TextInputLayout
    private lateinit var edtVacancyDesc: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var waitingDialog: AlertDialog
    private var name = ""
    private var shortDesc = ""
    private var desc = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_vacancy, container, false)
        initView(root)
        return root
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        tilVacancyName = root.findViewById(R.id.til_vacancy_name)
        edtVacancyName = root.findViewById(R.id.edt_vacancy_name)
        tilVacancyShortDesc = root.findViewById(R.id.til_vacancy_short_desc)
        edtVacancyShortDesc = root.findViewById(R.id.edt_vacancy_short_desc)
        tilVacancyDesc = root.findViewById(R.id.til_vacancy_desc)
        edtVacancyDesc = root.findViewById(R.id.edt_vacancy_desc)
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                name = edtVacancyName.text.toString().trim()
                tilVacancyName.error = null
                shortDesc = edtVacancyShortDesc.text.toString().trim().replace("\n", "<br>")
                tilVacancyShortDesc.error = null
                desc = edtVacancyDesc.text.toString().trim().replace("\n", "<br>")
                tilVacancyDesc.error = null
                if (name.isEmpty()) {
                    tilVacancyName.error = "Будь ласка, введіть назву вакансії"
                    return@setOnClickListener
                }
                if (shortDesc.isEmpty()) {
                    tilVacancyShortDesc.error = "Будь ласка, введіть короткий опис вакансії"
                    return@setOnClickListener
                }
                if (desc.isEmpty()) {
                    tilVacancyDesc.error = "Будь ласка, введіть повний опис вакансії"
                    return@setOnClickListener
                }
                edtImage()
            } else {
                Toast.makeText(
                    requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun edtImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                imageUri = data.data
                saveVacancy()
            }
        }
    }

    private fun saveVacancy() {
        val vacancy = VacancyModel()
        val vacancyRef =
            FirebaseDatabase.getInstance().getReference(Common.VACANCIES_REF)
        vacancy.id = vacancyRef.push().key.toString()
        waitingDialog.show()
        val vacancyFolder = storageReference.child(
            "vacancies/" + vacancy.id
        )
        vacancyFolder.putFile(imageUri!!).addOnFailureListener { e ->
            waitingDialog.dismiss()
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                vacancyFolder.downloadUrl.addOnSuccessListener { uri ->
                    vacancy.name = name
                    vacancy.image = imageUri.toString()
                    vacancy.desc = desc
                    vacancy.shortDesc = shortDesc
                    updateVacancy(vacancy)
                }
                waitingDialog.dismiss()
            }
        }.addOnProgressListener { taskSnapshot ->
            waitingDialog.setMessage("Завантаження")
        }
    }

    private fun updateVacancy(vacancyModel: VacancyModel) {
        FirebaseDatabase.getInstance().getReference(Common.VACANCIES_REF)
            .child(vacancyModel.id).setValue(vacancyModel)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно додано!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(VacanciesClick(true))
            }
    }
}
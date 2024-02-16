package com.sokol.pizzadreamadmin.ui.updateVacancy

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.NewsClick
import com.sokol.pizzadreamadmin.EventBus.VacanciesClick
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.Model.VacancyModel
import com.sokol.pizzadreamadmin.R
import com.sokol.pizzadreamadmin.ui.updateNews.UpdateNewsViewModel
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class UpdateVacancyFragment: Fragment() {

    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imgVacancy: ImageView
    private lateinit var tilVacancyName: TextInputLayout
    private lateinit var edtVacancyName: EditText
    private lateinit var tilVacancyShortDesc: TextInputLayout
    private lateinit var edtVacancyShortDesc: EditText
    private lateinit var tilVacancyDesc: TextInputLayout
    private lateinit var edtVacancyDesc: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var editImage: Button
    private lateinit var waitingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val updateVacancyViewModel = ViewModelProvider(this)[UpdateVacancyViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_update_vacancy, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            updateVacancyViewModel.getVacancyDetailMutableLiveData().observe(viewLifecycleOwner) {
                displayInfo(it)
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: VacancyModel) {
        Glide.with(requireContext()).load(it.image).into(imgVacancy)
        edtVacancyName.setText(it.name)
        edtVacancyShortDesc.setText(Html.fromHtml(it.shortDesc, Html.FROM_HTML_MODE_LEGACY))
        edtVacancyDesc.setText(Html.fromHtml(it.desc, Html.FROM_HTML_MODE_LEGACY))
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        imgVacancy = root.findViewById(R.id.img_vacancy)
        tilVacancyName = root.findViewById(R.id.til_vacancy_name)
        edtVacancyName = root.findViewById(R.id.edt_vacancy_name)
        tilVacancyShortDesc = root.findViewById(R.id.til_vacancy_short_desc)
        edtVacancyShortDesc = root.findViewById(R.id.edt_vacancy_short_desc)
        tilVacancyDesc = root.findViewById(R.id.til_vacancy_desc)
        edtVacancyDesc = root.findViewById(R.id.edt_vacancy_desc)
        editImage = root.findViewById(R.id.edit_image)
        editImage.setOnClickListener {
            edtImage()
        }
        imgVacancy.setOnClickListener {
            edtImage()
        }
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                val name = edtVacancyName.text.toString().trim()
                tilVacancyName.error = null
                val shortDesc = edtVacancyShortDesc.text.toString().trim().replace("\n", "<br>")
                tilVacancyShortDesc.error = null
                val desc = edtVacancyDesc.text.toString().trim().replace("\n", "<br>")
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
                val updateData = HashMap<String, Any>()
                updateData["name"] = name
                updateData["desc"] = desc
                updateData["shortDesc"] = shortDesc
                updateVacancy(updateData)
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

    private fun updateVacancy(updateData: HashMap<String, Any>) {
        FirebaseDatabase.getInstance().getReference(Common.VACANCIES_REF)
            .child(Common.vacancySelected!!.id).updateChildren(updateData)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно оновлено!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(VacanciesClick(true))
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                imageUri = data.data
                showDialogUpload()
            }
        }
    }

    private fun showDialogUpload() {
        val builder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        builder.setTitle("Змінити зображення").setMessage("Ви дійсно хочете змінити зображення?")
            .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Так") { dialogInterface, _ ->
                if (imageUri != null) {
                    if (Common.isConnectedToInternet(requireContext())) {
                        waitingDialog.show()
                        val imageFolder =
                            storageReference.child("vacancies/" + Common.vacancySelected!!.id)
                        imageFolder.putFile(imageUri!!).addOnFailureListener { e ->
                            waitingDialog.dismiss()
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                imageFolder.downloadUrl.addOnSuccessListener { uri ->
                                    val updateData = HashMap<String, Any>()
                                    updateData["image"] = uri.toString()
                                    updateVacancy(updateData)
                                }
                                waitingDialog.dismiss()
                            }
                        }.addOnProgressListener { taskSnapshot ->
                            waitingDialog.setMessage("Завантаження")
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Будь ласка, перевірте своє з'єднання!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }
}
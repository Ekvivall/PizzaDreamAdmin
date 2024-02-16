package com.sokol.pizzadreamadmin.ui.updateCategory

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.sokol.pizzadreamadmin.EventBus.MenuClick
import com.sokol.pizzadreamadmin.Model.CategoryModel
import com.sokol.pizzadreamadmin.R
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class UpdateCategoryFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var tilCategoryName: TextInputLayout
    private lateinit var edtCategoryName: EditText
    private lateinit var imgCategory: ImageView
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var editImage: Button
    private lateinit var waitingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val updateCategoryViewModel = ViewModelProvider(this)[UpdateCategoryViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_update_category, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            updateCategoryViewModel.getCategoryMutableLiveData().observe(viewLifecycleOwner) {
                displayInfo(it)
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: CategoryModel) {
        edtCategoryName.setText(it.name)
        Glide.with(requireContext()).load(it.image).into(imgCategory)
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        tilCategoryName = root.findViewById(R.id.til_category_name)
        edtCategoryName = root.findViewById(R.id.edt_category_name)
        imgCategory = root.findViewById(R.id.img_category)
        editImage = root.findViewById(R.id.edit_image)
        editImage.setOnClickListener {
            edtImage()
        }
        imgCategory.setOnClickListener {
            edtImage()
        }
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                val name = edtCategoryName.text.toString().trim()
                tilCategoryName.error = null
                if (name.isEmpty()) {
                    tilCategoryName.error = "Будь ласка, введіть назву категорії"
                    return@setOnClickListener
                }
                val updateData = HashMap<String, Any>()
                updateData["name"] = name
                updateCategory(updateData)
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

    private fun updateCategory(updateData: HashMap<String, Any>) {
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected!!.id!!).updateChildren(updateData)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно оновлено!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(MenuClick(true))
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
                            storageReference.child("icon_category/" + Common.categorySelected!!.id)
                        imageFolder.putFile(imageUri!!).addOnFailureListener { e ->
                            waitingDialog.dismiss()
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                imageFolder.downloadUrl.addOnSuccessListener { uri ->
                                    val updateData = HashMap<String, Any>()
                                    updateData["image"] = uri.toString()
                                    updateCategory(updateData)
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
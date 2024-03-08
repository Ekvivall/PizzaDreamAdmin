package com.sokol.pizzadreamadmin.ui.addCategory

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
import com.sokol.pizzadreamadmin.EventBus.MenuClick
import com.sokol.pizzadreamadmin.Model.CategoryModel
import com.sokol.pizzadreamadmin.R
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class AddCategoryFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var tilCategoryName: TextInputLayout
    private lateinit var edtCategoryName: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var waitingDialog: AlertDialog
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_category, container, false)
        initView(root)
        return root
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        tilCategoryName = root.findViewById(R.id.til_category_name)
        edtCategoryName = root.findViewById(R.id.edt_category_name)
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                name = edtCategoryName.text.toString().trim()
                tilCategoryName.error = null
                if (name.isEmpty()) {
                    tilCategoryName.error = "Будь ласка, введіть назву категорії"
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
                saveCategory()
            }
        }
    }

    private fun saveCategory() {
        val category = CategoryModel()
        val categoryRef =
            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
        category.id = categoryRef.push().key.toString()
        waitingDialog.show()
        val categoryFolder = storageReference.child(
            "icon_category/" + category.id
        )
        categoryFolder.putFile(imageUri!!).addOnFailureListener { e ->
            waitingDialog.dismiss()
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                categoryFolder.downloadUrl.addOnSuccessListener { uri ->
                    category.name = name
                    category.image = imageUri.toString()
                    category.foods = HashMap()
                    updateCategory(category)
                }
                waitingDialog.dismiss()
            }
        }.addOnProgressListener { taskSnapshot ->
            waitingDialog.setMessage("Завантаження")
        }
    }

    private fun updateCategory(categoryModel: CategoryModel) {
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF).child(categoryModel.id!!)
            .setValue(categoryModel)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно додано!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(MenuClick(true))
            }
    }
}
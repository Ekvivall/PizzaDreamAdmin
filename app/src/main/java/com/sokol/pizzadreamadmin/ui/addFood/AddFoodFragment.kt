package com.sokol.pizzadreamadmin.ui.addFood

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sokol.pizzadreamadmin.Adapter.SizeAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.CategoryClick
import com.sokol.pizzadreamadmin.EventBus.SelectSizeModel
import com.sokol.pizzadreamadmin.EventBus.UpdateSize
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.Model.SizeModel
import com.sokol.pizzadreamadmin.R
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AddFoodFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var tilFoodName: TextInputLayout
    private lateinit var edtFoodName: EditText
    private lateinit var tilFoodDesc: TextInputLayout
    private lateinit var edtFoodDesc: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var waitingDialog: AlertDialog
    private lateinit var spnAddonGroup: Spinner
    private lateinit var sizeRecycler: RecyclerView
    private lateinit var adapterSize: SizeAdapter
    private lateinit var edtNameSize: EditText
    private lateinit var tilNameSize: TextInputLayout
    private lateinit var edtPriceSize: EditText
    private lateinit var tilPriceSize: TextInputLayout
    private lateinit var addSize: Button
    private lateinit var editSize: Button
    private var needSaveSize = false
    private var name = ""
    private var desc = ""
    private var addon = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_food, container, false)
        initView(root)
        return root
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        tilFoodName = root.findViewById(R.id.til_food_name)
        edtFoodName = root.findViewById(R.id.edt_food_name)
        tilFoodDesc = root.findViewById(R.id.til_food_desc)
        edtFoodDesc = root.findViewById(R.id.edt_food_desc)
        btnSave = root.findViewById(R.id.save)
        spnAddonGroup = root.findViewById(R.id.spn_addon_group)
        val addonGroups = arrayOf("Без додаткових інгредієнтів", "Для піци", "Для суші")
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, addonGroups
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnAddonGroup.adapter = adapter
        sizeRecycler = root.findViewById(R.id.size_recycler)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        sizeRecycler.layoutManager = layoutManager
        adapterSize = SizeAdapter(Common.sizeSelected.toMutableList(), requireContext())
        sizeRecycler.adapter = adapterSize
        edtNameSize = root.findViewById(R.id.edt_size_name)
        tilNameSize = root.findViewById(R.id.til_size_name)
        tilPriceSize = root.findViewById(R.id.til_size_price)
        edtPriceSize = root.findViewById(R.id.edt_size_price)
        addSize = root.findViewById(R.id.create_size)
        editSize = root.findViewById(R.id.edit_size)
        editSize.setOnClickListener {
            handleSizeButtonClick(true)
        }
        addSize.setOnClickListener {
            handleSizeButtonClick(false)
        }
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                name = edtFoodName.text.toString().trim()
                tilFoodName.error = null
                desc = edtFoodDesc.text.toString().trim().replace("\n", "<br>")
                tilFoodDesc.error = null
                tilNameSize.error = null
                if (name.isEmpty()) {
                    tilFoodName.error = "Будь ласка, введіть назву страви"
                    return@setOnClickListener
                }
                if (desc.isEmpty()) {
                    tilFoodDesc.error = "Будь ласка, введіть опис страви"
                    return@setOnClickListener
                }
                if (Common.sizeSelected.isEmpty()) {
                    tilNameSize.error = "Будь ласка, додайте хоча б один розмір"
                    return@setOnClickListener
                }
                addon = when (spnAddonGroup.selectedItemPosition) {
                    1 -> {
                        "pizza"
                    }

                    2 -> {
                        "sushi"
                    }

                    else -> {
                        ""
                    }
                }
                edtImage()
            } else {
                Toast.makeText(
                    requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun handleSizeButtonClick(isEdit: Boolean) {
        val name = edtNameSize.text.toString().trim()
        tilNameSize.error = null
        val price = edtPriceSize.text.toString().trim()
        tilPriceSize.error = null
        if (name.isEmpty()) {
            tilNameSize.error = "Будь ласка, введіть назву розміру страви"
            return
        }
        if (price.isEmpty()) {
            tilPriceSize.error = "Будь ласка, введіть ціну розміру страви"
            return
        }
        val sizeModel = SizeModel()
        sizeModel.name = name
        sizeModel.price = price.toInt()
        if (isEdit) {
            adapterSize.editSize(sizeModel)
        } else {
            adapterSize.addSize(sizeModel)
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
                saveFood()
            }
        }
    }

    private fun saveFood() {
        val food = FoodModel()
        val foodRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected!!.id!!).child("foods")
        food.id = foodRef.push().key.toString()
        waitingDialog.show()
        val foodFolder = storageReference.child(
            "icon_food/" + food.id
        )
        foodFolder.putFile(imageUri!!).addOnFailureListener { e ->
            waitingDialog.dismiss()
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                foodFolder.downloadUrl.addOnSuccessListener { uri ->
                    food.categoryId = Common.categorySelected!!.id!!
                    food.name = name
                    food.image = uri.toString()
                    food.description = desc
                    food.addon = addon
                    food.size = Common.sizeSelected
                    food.ratingSum = 0L
                    food.ratingCount = 0L
                    saveFood(food)
                }
                waitingDialog.dismiss()
            }
        }.addOnProgressListener { taskSnapshot ->
            waitingDialog.setMessage("Завантаження")
        }
    }

    private fun saveFood(food: FoodModel) {
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected!!.id!!).child("foods").child(food.id!!).setValue(food)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Common.categorySelected!!.foods?.put(food.id!!, food)
                Toast.makeText(requireContext(), "Інформацію успішно оновлено!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(CategoryClick(true))
            }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSizeModelUpdate(event: UpdateSize) {
        needSaveSize = true
        Common.sizeSelected = event.sizeModelList
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSelectSizeEvent(event: SelectSizeModel) {
        edtNameSize.setText(event.sizeModel.name)
        edtPriceSize.setText(event.sizeModel.price.toString())
        editSize.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }
}
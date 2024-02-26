package com.sokol.pizzadreamadmin.ui.updateFood

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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

class UpdateFoodFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imgFood: ImageView
    private lateinit var tilFoodName: TextInputLayout
    private lateinit var edtFoodName: EditText
    private lateinit var tilFoodDesc: TextInputLayout
    private lateinit var edtFoodDesc: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var editImage: Button
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val updateFoodViewModel = ViewModelProvider(this)[UpdateFoodViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_update_food, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            updateFoodViewModel.getFoodDetailMutableLiveData().observe(viewLifecycleOwner) {
                displayInfo(it)
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: FoodModel) {
        Glide.with(requireContext()).load(it.image).into(imgFood)
        edtFoodName.setText(it.name)
        edtFoodDesc.setText(Html.fromHtml(it.description, Html.FROM_HTML_MODE_LEGACY))
        if (it.addon == "pizza") {
            spnAddonGroup.setSelection(1)
        } else if (it.addon == "sushi") {
            spnAddonGroup.setSelection(2)
        }
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        imgFood = root.findViewById(R.id.img_food)
        editImage = root.findViewById(R.id.edit_image)
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
        adapterSize =
            SizeAdapter(Common.foodSelected!!.size.toMutableList(), requireContext())
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
        editImage.setOnClickListener {
            edtImage()
        }
        imgFood.setOnClickListener {
            edtImage()
        }
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                val name = edtFoodName.text.toString().trim()
                tilFoodName.error = null
                val desc = edtFoodDesc.text.toString().trim().replace("\n", "<br>")
                tilFoodDesc.error = null
                if (name.isEmpty()) {
                    tilFoodName.error = "Будь ласка, введіть назву страви"
                    return@setOnClickListener
                }
                if (desc.isEmpty()) {
                    tilFoodDesc.error = "Будь ласка, введіть опис страви"
                    return@setOnClickListener
                }
                val updateData = HashMap<String, Any>()
                val food = Common.foodSelected!!
                updateData["name"] = name
                updateData["description"] = desc
                val addon = when (spnAddonGroup.selectedItemPosition) {
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
                updateData["addon"] = addon
                food.name = name
                food.description = desc
                food.addon = addon
                Common.categorySelected?.foods?.replace(food.id!!, food)
                if(needSaveSize){
                    saveSize()
                }
                updateFood(updateData)
            } else {
                Toast.makeText(
                    requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveSize() {
        val updateData: MutableMap<String, Any> = HashMap()
        updateData["size"] = Common.foodSelected!!.size
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.foodSelected?.categoryId.toString()).child("foods")
            .child(Common.foodSelected?.id.toString()).updateChildren(updateData)
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener { task ->
                needSaveSize = false
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

    private fun updateFood(updateData: HashMap<String, Any>) {
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.foodSelected!!.categoryId!!).child("foods")
            .child(Common.foodSelected!!.id!!).updateChildren(updateData)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно оновлено!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(CategoryClick(true))
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
                            storageReference.child("icon_food/" + Common.foodSelected!!.id)
                        imageFolder.putFile(imageUri!!).addOnFailureListener { e ->
                            waitingDialog.dismiss()
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                imageFolder.downloadUrl.addOnSuccessListener { uri ->
                                    val updateData = HashMap<String, Any>()
                                    updateData["image"] = uri.toString()
                                    val food = Common.foodSelected!!
                                    food.image = uri.toString()
                                    Common.categorySelected?.foods?.replace(food.id!!, food)
                                    updateFood(updateData)
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSizeModelUpdate(event: UpdateSize) {
        needSaveSize = true
        Common.foodSelected!!.size = event.sizeModelList
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSelectSizeEvent(event: SelectSizeModel) {
        edtNameSize.setText(event.sizeModel.name)
        edtPriceSize.setText(event.sizeModel.price.toString())
        editSize.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }
}
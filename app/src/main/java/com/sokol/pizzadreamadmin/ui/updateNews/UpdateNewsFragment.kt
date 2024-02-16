package com.sokol.pizzadreamadmin.ui.updateNews

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
import com.sokol.pizzadreamadmin.EventBus.MenuClick
import com.sokol.pizzadreamadmin.EventBus.NewsClick
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.R
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class UpdateNewsFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imgNews: ImageView
    private lateinit var tilNewsTitle: TextInputLayout
    private lateinit var edtNewsTitle: EditText
    private lateinit var tilNewsContent: TextInputLayout
    private lateinit var edtNewsContent: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var editImage: Button
    private lateinit var waitingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val udateNewsViewModel = ViewModelProvider(this)[UpdateNewsViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_update_news, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            udateNewsViewModel.getNewsDetailMutableLiveData().observe(viewLifecycleOwner) {
                displayInfo(it)
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: NewsModel) {
        Glide.with(requireContext()).load(it.image).into(imgNews)
        edtNewsTitle.setText(it.title)
        edtNewsContent.setText(Html.fromHtml(it.content, Html.FROM_HTML_MODE_LEGACY))
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        imgNews = root.findViewById(R.id.img_news)
        tilNewsTitle = root.findViewById(R.id.til_news_title)
        edtNewsTitle = root.findViewById(R.id.edt_news_title)
        tilNewsContent = root.findViewById(R.id.til_news_content)
        edtNewsContent = root.findViewById(R.id.edt_news_content)
        editImage = root.findViewById(R.id.edit_image)
        editImage.setOnClickListener {
            edtImage()
        }
        imgNews.setOnClickListener {
            edtImage()
        }
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                val title = edtNewsTitle.text.toString().trim()
                tilNewsTitle.error = null
                val content = edtNewsContent.text.toString().trim().replace("\n", "<br>")
                tilNewsContent.error = null
                if (title.isEmpty()) {
                    tilNewsTitle.error = "Будь ласка, введіть заголовок новини"
                    return@setOnClickListener
                }
                if (content.isEmpty()) {
                    tilNewsContent.error = "Будь ласка, введіть зміст новини"
                    return@setOnClickListener
                }
                val updateData = HashMap<String, Any>()
                updateData["title"] = title
                updateData["content"] = content
                updateNews(updateData)
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

    private fun updateNews(updateData: HashMap<String, Any>) {
        FirebaseDatabase.getInstance().getReference(Common.NEWS_REF)
            .child(Common.newsSelected!!.id!!).updateChildren(updateData)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(requireContext(), "Інформацію успішно оновлено!", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().postSticky(NewsClick(true))
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
                            storageReference.child("news/" + Common.newsSelected!!.id)
                        imageFolder.putFile(imageUri!!).addOnFailureListener { e ->
                            waitingDialog.dismiss()
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                imageFolder.downloadUrl.addOnSuccessListener { uri ->
                                    val updateData = HashMap<String, Any>()
                                    updateData["image"] = uri.toString()
                                    updateNews(updateData)
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
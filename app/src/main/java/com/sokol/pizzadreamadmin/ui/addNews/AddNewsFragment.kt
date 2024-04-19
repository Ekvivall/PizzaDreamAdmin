package com.sokol.pizzadreamadmin.ui.addNews

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
import com.sokol.pizzadreamadmin.EventBus.NewsClick
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.R
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.util.Calendar

class AddNewsFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var tilNewsTitle: TextInputLayout
    private lateinit var edtNewsTitle: EditText
    private lateinit var tilNewsContent: TextInputLayout
    private lateinit var edtNewsContent: EditText
    private val PICK_IMAGE_REQUEST = 7272
    private lateinit var btnSave: Button
    private lateinit var waitingDialog: AlertDialog
    private var title = ""
    private var content = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_news, container, false)
        initView(root)
        return root
    }

    private fun initView(root: View) {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialog =
            SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        tilNewsTitle = root.findViewById(R.id.til_news_title)
        edtNewsTitle = root.findViewById(R.id.edt_news_title)
        tilNewsContent = root.findViewById(R.id.til_news_content)
        edtNewsContent = root.findViewById(R.id.edt_news_content)
        btnSave = root.findViewById(R.id.save)
        btnSave.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                title = edtNewsTitle.text.toString().trim()
                tilNewsTitle.error = null
                content = edtNewsContent.text.toString().trim().replace("\n", "<br>")
                tilNewsContent.error = null
                if (title.isEmpty()) {
                    tilNewsTitle.error = "Будь ласка, введіть заголовок новини"
                    return@setOnClickListener
                }
                if (content.isEmpty()) {
                    tilNewsContent.error = "Будь ласка, введіть зміст новини"
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
                saveNews()
            }
        }
    }

    private fun saveNews() {
        val news = NewsModel()
        val newsRef = FirebaseDatabase.getInstance().getReference(Common.NEWS_REF)
        news.id = newsRef.push().key.toString()
        waitingDialog.show()
        val newsFolder = storageReference.child(
            "news/" + news.id
        )
        newsFolder.putFile(imageUri!!).addOnFailureListener { e ->
            waitingDialog.dismiss()
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newsFolder.downloadUrl.addOnSuccessListener { uri ->
                    news.title = title
                    news.image = uri.toString()
                    news.content = content
                    news.date = Calendar.getInstance().timeInMillis
                    updateNews(news)
                }
                waitingDialog.dismiss()
            }
        }.addOnProgressListener { taskSnapshot ->
            waitingDialog.setMessage("Завантаження")
        }
    }

    private fun updateNews(newsModel: NewsModel) {
        FirebaseDatabase.getInstance().getReference(Common.NEWS_REF)
            .child(newsModel.id!!).setValue(newsModel)
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(
                    requireContext(), "Інформацію успішно додано!", Toast.LENGTH_SHORT
                ).show()
                EventBus.getDefault().postSticky(NewsClick(true))
            }
    }
}
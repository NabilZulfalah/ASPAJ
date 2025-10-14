package com.example.androidphpmysql

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidphpmysql.SharedPrefManager
import java.io.ByteArrayOutputStream
import java.io.IOException

class pengembalian : AppCompatActivity() {

    private lateinit var spinnerCondition: Spinner
    private lateinit var imageViewPhoto: ImageView
    private lateinit var buttonUploadPhoto: Button
    private lateinit var buttonSubmitReturn: Button

    private var selectedImage: Bitmap? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var borrowingId: Int = -1
    private lateinit var progressDialog: android.app.ProgressDialog

    private val kondisiList = arrayOf("Baik", "Rusak Ringan", "Rusak Berat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_form)

        spinnerCondition = findViewById(R.id.spinnerCondition)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)
        buttonUploadPhoto = findViewById(R.id.buttonUploadPhoto)
        buttonSubmitReturn = findViewById(R.id.buttonSubmitReturn)

        progressDialog = android.app.ProgressDialog(this)
        progressDialog.setCancelable(false)

        // Get borrowing ID from intent
        borrowingId = intent.getIntExtra("borrowing_id", -1)

        // Set up spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kondisiList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCondition.adapter = adapter

        // Setup image picker launcher
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imageViewPhoto.setImageBitmap(selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonUploadPhoto.setOnClickListener { openImagePicker() }

        buttonSubmitReturn.setOnClickListener { submitReturn() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private fun submitReturn() {
        if (selectedImage == null) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show()
            return
        }

        if (borrowingId == -1) {
            Toast.makeText(this, "Invalid borrowing ID", Toast.LENGTH_SHORT).show()
            return
        }

        progressDialog.setMessage("Submitting return...")
        progressDialog.show()

        val condition = spinnerCondition.selectedItem.toString()

        // Convert bitmap to base64
        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedImage?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val url = "http://192.168.0.113:8000/api/return-borrowing"
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                progressDialog.dismiss()
                Toast.makeText(this, "Return submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error submitting return: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["borrowing_id"] = borrowingId.toString()
                params["condition"] = condition
                params["photo"] = encodedImage
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${SharedPrefManager.getInstance(this@pengembalian).getToken()}"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

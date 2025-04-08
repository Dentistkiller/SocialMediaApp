package com.fake.socialmediaapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.time.LocalDate

class PostActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var chosenImageUri: Uri
    private lateinit var btnChooseImage : Button
    private lateinit var btnUploadImage : Button
    private lateinit var imgPreview : ImageView
    private lateinit var etCaption : EditText
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post)


        val userName = intent.getStringExtra("userName")
        Log.d("UsernameCheckLoggedIn", "USERNAME is==> $userName")

        firestore = FirebaseFirestore.getInstance()

        btnChooseImage = findViewById(R.id.btn_choose_image)
        btnUploadImage = findViewById(R.id.btn_upload_image)
        imgPreview = findViewById(R.id.img_preview)
        etCaption = findViewById(R.id.etCaption)
        bottomNav = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.profile -> {
                    Toast.makeText(this,"coming soon!!!",Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.upload -> {
                    val intent = Intent(this, PostActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        btnChooseImage.setOnClickListener {
            pickImageFromGallery()
        }

        btnUploadImage.setOnClickListener {
            if (::chosenImageUri.isInitialized) {
                val base64Image = convertImageToBase64(imgPreview)
                val caption = etCaption.text.toString()
                val date = LocalDate.now().toString()
                val username = userName ?: "Unknown"

                saveImageToFirestore(base64Image, username, caption, date)
            } else {
                Log.d("image upload", "No image detected")
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            chosenImageUri = data?.data!!
            imgPreview.setImageURI(chosenImageUri)
        }
    }


    //converting image to string
    private fun convertImageToBase64(imageView: ImageView): String {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream) //comprerssion to save into firestore --1mb limit
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun saveImageToFirestore(imageBase64: String, username: String, caption: String, date: String) {
        val imageData = hashMapOf(
            "imageBase64" to imageBase64,
            "username" to username,
            "caption" to caption,
            "date" to date,
            "likes" to "0",
            "hates" to "0"
        )
        firestore.collection("posts")
            .add(imageData)
            .addOnSuccessListener {
                Log.d("Firestore", "Image saved successfully")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to save image: $exception")
            }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}
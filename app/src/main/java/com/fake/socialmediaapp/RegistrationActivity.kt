package com.fake.socialmediaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var usernameEt: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        usernameEt = findViewById(R.id.etUserame)
        registerButton = findViewById(R.id.registerButton)
        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val username = usernameEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    registerUser(email, password)
                    createProfile(email, username)
                    Toast.makeText(this,"User created sucessfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter email, password, and confirm password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, navigate to the login screen
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    Log.d("register", "Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun createProfile(email: String, username: String){
        if (email.isNotEmpty() && username.isNotEmpty()) {
            val profile = hashMapOf(
                "email" to email,
                "username" to username
            )
            db.collection("users").document(username).set(profile)
                .addOnSuccessListener { documentReference ->
                    Log.d("Profile Creation","Successfully created profile")
                    //db.collection("users").document(username).collection("posts").add(username)
                }
                .addOnFailureListener { e ->
                    Log.d("Profile Creation","Failed when creating profile")
                }

        }
    }

    fun loginButton(view: View) {
        val loginBtn = Intent(this@RegistrationActivity, LoginActivity::class.java)
        startActivity(loginBtn)
    }
}
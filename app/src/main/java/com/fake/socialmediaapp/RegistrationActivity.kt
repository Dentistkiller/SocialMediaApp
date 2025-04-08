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
    private lateinit var loginPageButton: Button
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
        loginPageButton = findViewById(R.id.loginPageButton)
        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val username = usernameEt.text.toString().trim()

            //if nothing is empty
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                //and if password matches confirm password
                if (password == confirmPassword) {
                    //calls first method to register with firebase auth
                    registerUser(email, password)
                    //creates the  user profile in firestore
                    createProfile(email, username)
                    Toast.makeText(this,"User created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter email, password, and confirm password", Toast.LENGTH_SHORT).show()
            }
        }

        loginPageButton.setOnClickListener {
            val loginBtn = Intent(this, LoginActivity::class.java)
            startActivity(loginBtn)
        }

    }

    //method to save user details to firebase auth
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

    //method saves details to user profile on firestore
    private fun createProfile(email: String, username: String){
        if (email.isNotEmpty() && username.isNotEmpty()) {
            //creates a hash map
            // hash map creates a json object to store in no sql db
            //maps each of our fields to a field in the json/Firestore document
            //recommend keeping both names the same to avoid confusion
            val profile = hashMapOf(
                "email" to email,
                "username" to username
            )

            //calls our firestore database
            //chooses our specified collection to save the data-- in this case "users"
            //Creates a new document with the username
            //Sets  the data in the document with our map called "profile"
            db.collection("users").document(username).set(profile)
                //checks if saving th data successful
                .addOnSuccessListener { documentReference ->
                    Log.d("Profile Creation","Successfully created profile")
                    //db.collection("users").document(username).collection("posts").add(username)
                }
                //checks if saving th data is unsuccessful
                .addOnFailureListener { e ->
                    Log.d("Profile Creation","Failed when creating profile")
                }

        }
    }
}
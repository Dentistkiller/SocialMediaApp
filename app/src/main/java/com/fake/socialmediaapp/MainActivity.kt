package com.fake.socialmediaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fake.fakebook.Posts
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    private lateinit var postsRecyclerView: RecyclerView
    private val posts = mutableListOf<Posts>() // An empty list initially
    private lateinit var adapter: PostAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val userName = intent.getStringExtra("email")
        postsRecyclerView = findViewById(R.id.postRecyclerView)
        adapter = PostAdapter(posts)
        postsRecyclerView.adapter = adapter
        bottomNav = findViewById(R.id.bottomNav)
        val layoutManager = LinearLayoutManager(this)
        postsRecyclerView.layoutManager = layoutManager

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
                else -> {false}
            }
        }
        fetchPostsFromFirestore()
    }
    private fun fetchPostsFromFirestore() {
        db.collection("posts") // Replace with your collection name
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val post = document.toObject(Posts::class.java)!! // Assuming data matches Post class
                    posts.add(post)
                }
                adapter.notifyDataSetChanged() // Update RecyclerView adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchCommentsFromFirestore() {
        db.collection("posts").document("your document id").collection("comments")
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val post = document.toObject(Posts::class.java)!! // Assuming data matches Post class
                    posts.add(post)
                }
                adapter.notifyDataSetChanged() // Update RecyclerView adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"error", Toast.LENGTH_SHORT).show()
            }
    }
}


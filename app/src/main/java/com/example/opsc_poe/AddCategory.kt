package com.example.opsc_poe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class AddCategory : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var categoryListView: ListView
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val categoryList: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        db = FirebaseFirestore.getInstance()
        categoryListView = findViewById(R.id.categoryListView)

        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList)
        categoryListView.adapter = categoryAdapter

        // Load categories from Firestore
        loadCategories()

        findViewById<Button>(R.id.saveCategoryButton).setOnClickListener {
            val categoryName = findViewById<EditText>(R.id.categoryNameEditText).text.toString()
            if (categoryName.isNotEmpty()) {
                val categoryId = db.collection("categories").document().id
                val category = Category(id = categoryId, name = categoryName)
                Log.d("AddCategoryActivity", "Attempting to save category: $category")
                db.collection("categories").document(categoryId).set(category)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("AddCategoryActivity", "Category added successfully: $categoryId")
                            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                            categoryList.add(categoryName)
                            categoryAdapter.notifyDataSetChanged()
                            findViewById<EditText>(R.id.categoryNameEditText).text.clear()
                            startActivity(Intent(this, AddTimesheetEntry::class.java))

                        } else {
                            Log.e("AddCategoryActivity", "Failed to add category", task.exception)
                            Toast.makeText(this, "Failed to add category: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Log.d("AddCategoryActivity", "Category name is empty")
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }private fun loadCategories() {
        db.collection("categories").get().addOnSuccessListener { result ->
            categoryList.clear()
            for (document in result) {
                val category = document.toObject(Category::class.java)
                categoryList.add(category.name)
            }
            categoryAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load categories: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

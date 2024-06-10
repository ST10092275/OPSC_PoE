package com.example.opsc_poe

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddTimesheetEntry : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var categoryList: MutableList<Category>
    private lateinit var categorySpinner: Spinner
    private lateinit var timesheetListView: ListView
    private lateinit var timesheetAdapter: ArrayAdapter<String>
    private val timesheetEntries: MutableList<TimesheetEntry> = mutableListOf()

    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timesheet_entry)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        categoryList = mutableListOf()
        categorySpinner = findViewById(R.id.categorySpinner)
        timesheetListView = findViewById(R.id.timesheetListView)
        timesheetAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, timesheetEntries.map { entry -> "${entry.date.toDate()} - ${entry.category} - ${entry.getTotalHours()} hours" })
        timesheetListView.adapter = timesheetAdapter

        loadCategories()
        loadTimesheetEntries()

        findViewById<Button>(R.id.addPhotoButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        findViewById<Button>(R.id.saveTimesheetEntryButton).setOnClickListener {
            saveTimesheetEntry()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            findViewById<ImageView>(R.id.photoImageView).apply {
                visibility = View.VISIBLE
                setImageURI(selectedPhotoUri)
            }
        }
    }

    private fun loadCategories() {
        db.collection("categories").get().addOnSuccessListener { result ->
            categoryList.clear()
            for (document in result) {
                val category = document.toObject(Category::class.java)
                categoryList.add(category)
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }
    }

    private fun loadTimesheetEntries() {
        db.collection("timesheet_entries").get().addOnSuccessListener { result ->
            timesheetEntries.clear()
            for (document in result) {
                val entry = document.toObject(TimesheetEntry::class.java)
                timesheetEntries.add(entry)
            }
            updateTimesheetListView()
        }
    }

    private fun updateTimesheetListView() {
        val displayList = timesheetEntries.map { entry -> "${entry.date.toDate()} - ${entry.category} - ${entry.getTotalHours()} hours" }
        timesheetAdapter.clear()
        timesheetAdapter.addAll(displayList)
        timesheetAdapter.notifyDataSetChanged()
    }

    private fun saveTimesheetEntry() {
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val startTimeEditText = findViewById<EditText>(R.id.startTimeEditText)
        val endTimeEditText = findViewById<EditText>(R.id.endTimeEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val category = categorySpinner.selectedItem.toString()

        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())

        val parsedDate = sdfDate.parse(dateEditText.text.toString())
        val parsedStartTime = sdfTime.parse(startTimeEditText.text.toString())
        val parsedEndTime = sdfTime.parse(endTimeEditText.text.toString())

        if (parsedDate == null || parsedStartTime == null || parsedEndTime == null || descriptionEditText.text.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields with correct format", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val newTimesheetEntry = TimesheetEntry(
            id = UUID.randomUUID().toString(),
            date = Timestamp(parsedDate),
            startTime = Timestamp(parsedStartTime),
            endTime = Timestamp(parsedEndTime),
            description = descriptionEditText.text.toString(),
            category = category
        )

        if (selectedPhotoUri != null) {
            val photoRef = storage.reference.child("timesheet_photos/${newTimesheetEntry.id}")
            photoRef.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        saveEntryToDatabase(newTimesheetEntry.copy(photoUrl = uri.toString()))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
        } else {
            saveEntryToDatabase(newTimesheetEntry)
        }
    }

    private fun saveEntryToDatabase(entry: TimesheetEntry) {
        db.collection("timesheet_entries").document(entry.id).set(entry)
            .addOnSuccessListener {
                Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show()
                timesheetEntries.add(entry)
                updateTimesheetListView()
                clearForm()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearForm() {
        findViewById<EditText>(R.id.dateEditText).text.clear()
        findViewById<EditText>(R.id.startTimeEditText).text.clear()
        findViewById<EditText>(R.id.endTimeEditText).text.clear()
        findViewById<EditText>(R.id.descriptionEditText).text.clear()
        findViewById<ImageView>(R.id.photoImageView).apply {
            setImageURI(null)
            visibility = View.GONE
        }
        selectedPhotoUri = null
    }
}


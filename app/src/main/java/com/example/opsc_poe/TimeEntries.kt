package com.example.opsc_poe

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TimeEntries : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var timesheetEntriesListView: ListView
    private lateinit var timesheetEntriesAdapter: ArrayAdapter<String>
    private val categories = mutableListOf<Category>()
    private val timesheetEntries = mutableListOf<TimesheetEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_time_entries)

        database = FirebaseDatabase.getInstance().reference
        timesheetEntriesListView = findViewById(R.id.timesheetEntriesListView)

        timesheetEntriesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, timesheetEntries.map { it.description })

        timesheetEntriesListView.adapter = timesheetEntriesAdapter

        loadTimesheetEntries()
    }
    private fun loadTimesheetEntries() {
        database.child("timesheetEntries").addValueEventListener(object : ValueEventListener {
           override fun onDataChange(snapshot: DataSnapshot) {
                timesheetEntries.clear()
                for (entrySnapshot in snapshot.children) {
                    val entry = entrySnapshot.getValue(TimesheetEntry::class.java)
                    if (entry != null) {
                        timesheetEntries.add(entry)
                    }
                }
                timesheetEntriesAdapter.clear()
                timesheetEntriesAdapter.addAll(timesheetEntries.map { it.description })
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}


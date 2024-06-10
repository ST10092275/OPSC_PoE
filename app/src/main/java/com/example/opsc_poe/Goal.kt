package com.example.opsc_poe

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Goal : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var goalsRef: com.google.firebase.firestore.CollectionReference
    private lateinit var btnSaveGoals: Button
    private lateinit var etGoalName: EditText
    private lateinit var seekbarMinGoal: SeekBar
    private lateinit var seekbarMaxGoal: SeekBar
    private lateinit var btnSelectDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var goalList: ListView
    private lateinit var tvMinGoal: TextView
    private lateinit var tvMaxGoal: TextView

    private var selectedDate: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_goal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize UI components
        btnSaveGoals = findViewById(R.id.btnSaveGoals)
        etGoalName = findViewById(R.id.etGoalName)
        seekbarMinGoal = findViewById(R.id.seekbarMinGoal)
        seekbarMaxGoal = findViewById(R.id.seekbarMaxGoal)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        goalList = findViewById(R.id.goalList)
        tvMinGoal = findViewById(R.id.tvMinGoal)
        tvMaxGoal = findViewById(R.id.tvMaxGoal)

        db = FirebaseFirestore.getInstance()
        goalsRef = db.collection("goals")

        seekbarMinGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMinGoal.text = "Min Goal: $progress hours"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekbarMaxGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMaxGoal.text = "Max Goal: $progress hours"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnSaveGoals.setOnClickListener {
            val name = etGoalName.text.toString()
            val minGoal = seekbarMinGoal.progress
            val maxGoal = seekbarMaxGoal.progress

            if (name.isNotEmpty() && minGoal <= maxGoal) {
                val goal = Goals(name, minGoal, maxGoal)
                goalsRef.add(goal).addOnSuccessListener {
                    loadGoals()
                }.addOnFailureListener { e ->
                    // Handle failure
                }
            } else {
                // Show error message
            }
        }

        loadGoals()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tvSelectedDate.text = sdf.format(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadGoals() {
        goalsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val goals = task.result.toObjects(Goals::class.java)
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, goals)
                goalList.adapter = adapter
            } else {
                // Handle error
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.goal_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.goalss -> {
                startActivity(Intent(this, Goal::class.java))
                return true
            }
            R.id.homepages -> {
                startActivity(Intent(this, Homepage::class.java))
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}

package com.example.opsc7311_poe

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GoalsManager(private val activity: Activity) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val etGoalName: EditText = activity.findViewById(R.id.et_goal_name)
    private val tvMinGoal: TextView = activity.findViewById(R.id.tv_min_goal)
    private val tvMaxGoal: TextView = activity.findViewById(R.id.tv_max_goal)
    private val seekBarMinGoal: SeekBar = activity.findViewById(R.id.seekBar_min_goal)
    private val seekBarMaxGoal: SeekBar = activity.findViewById(R.id.seekBar_max_goal)
    private val btnSaveGoals: Button = activity.findViewById(R.id.btn_save_goals)
    private val goalList: ListView = activity.findViewById(R.id.GoalList)

    private val goalsList: MutableList<String> = mutableListOf()
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, goalsList)
    }
    init {
        // Set SeekBar change listeners
        seekBarMinGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMinGoal.text = activity.getString(R.string.minimum_daily_goal, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarMaxGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMaxGoal.text = activity.getString(R.string.maximum_daily_goal, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set button click listener to save goals
        btnSaveGoals.setOnClickListener {
            saveGoals()
        }
        goalList.adapter = adapter

        // Fetch and display goals from Firebase
        fetchGoalsFromFirebase()
    }
    private fun fetchGoalsFromFirebase() {
        val goalsRef = database.child("goals")
        goalsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                goalsList.clear() // Clear existing goals
                for (childSnapshot in dataSnapshot.children) {
                    val goal = childSnapshot.getValue(Goal::class.java)
                    goal?.let {
                        val goalDetails =
                            "${it.name} - Min: ${it.minHours} hours, Max: ${it.maxHours} hours"
                        goalsList.add(goalDetails)
                    }
                }
                adapter.notifyDataSetChanged() // Notify the adapter that data has changed
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "fetchGoalsFromFirebase:onCancelled", databaseError.toException())
            }
        })
    }

    private fun saveGoals() {
        val goalName = etGoalName.text.toString()
        val minGoalHours = seekBarMinGoal.progress
        val maxGoalHours = seekBarMaxGoal.progress

        val goalsRef = database.child("goals")
        val goalId = goalsRef.push().key ?: ""
        goalsRef.child(goalId).setValue(Goal(goalName, minGoalHours, maxGoalHours))
    }

    data class Goal(val name: String, val minHours: Int, val maxHours: Int)
}
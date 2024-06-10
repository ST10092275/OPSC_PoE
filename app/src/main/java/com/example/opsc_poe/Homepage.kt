package com.example.opsc_poe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val goal = findViewById<Button>(R.id.goals)
        val time_entry = findViewById<Button>(R.id.time_entry)
        val logout = findViewById<Button>(R.id.log_out)

        goal.setOnClickListener {
            startActivity(Intent(this, Goal::class.java))
        }

        time_entry.setOnClickListener {
            startActivity(Intent(this, AddCategory::class.java))
        }

        logout.setOnClickListener {
            startActivity(Intent(this,logout::class.java))
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_item2 -> {
                startActivity(Intent(this, Goal::class.java))
                return true
            }
            R.id.menu_item3 -> {
                startActivity(Intent(this, Login::class.java))
                return true
            }
           R.id.timesheetentry ->{
               startActivity(Intent(this, AddCategory::class.java))
           }
        }
        return super.onOptionsItemSelected(item)
    }
}

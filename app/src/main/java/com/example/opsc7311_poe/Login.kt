package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.email)
        val editTextTextPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val login = findViewById<Button>(R.id.login)
        val regis_link = findViewById<TextView>(R.id.regis_link)

        login.setOnClickListener {
            val email = email.text.toString()
            val password = editTextTextPassword.text.toString()
            loginUser(email, password)
        }

        regis_link.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful) {
                    Toast.makeText(baseContext, "Login Succesful.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Toast.makeText(baseContext, "Login failed: ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
    }

}

package com.example.opsc_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.Password)
        val rRegister = findViewById<Button>(R.id.rRegister)
        val login = findViewById<TextView>(R.id.login)

        rRegister.setOnClickListener{
            val email = email.text.toString()
            val password = password.text.toString()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            registerUser(email, password )
        }
        login.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }
    }
    private fun registerUser(email:String, password:String ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    Toast.makeText(baseContext, "User Registration Successful.", Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(baseContext, " Registration failed : ${task.exception?.message}", Toast.LENGTH_SHORT).show()

                }

            }
    }
}
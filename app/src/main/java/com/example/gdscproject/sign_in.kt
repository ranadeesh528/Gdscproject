package com.example.gdscproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class sign_in : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginbtn: Button
    private lateinit var signuptxt1: TextView
    private lateinit var forgotpass1: TextView

    // Admin credentials (replace these with your actual admin credentials)
    private val adminUsername = "gdsc@gmail.com"
    private val adminPassword = "gdsc1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        emailInput = findViewById(R.id.inputuser)
        passwordInput = findViewById(R.id.inputpass)
        loginbtn = findViewById(R.id.btnLogin)
        signuptxt1 = findViewById(R.id.signuptxt)
        forgotpass1 = findViewById(R.id.forgotpass)

        loginbtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (email == adminUsername && password == adminPassword) {
                    // Admin credentials entered, redirect to administration activity
                    val intent = Intent(this, Administratorpage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    loginUser(email, password)
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signuptxt1.setOnClickListener {
            val intent = Intent(this, sign_up::class.java)
            startActivity(intent)
            finish()
        }

        forgotpass1.setOnClickListener {
            val intent = Intent(this, resetpassword::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Retrieve username after successful login
                    val username = email.substringBefore('@') // Example: extract username from email
                    val intent = Intent(this, homepage::class.java).apply {
                        putExtra("username", username) // Pass username to homepage activity
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    task.exception?.let { e ->
                        e.printStackTrace()
                    }
                }
            }
    }
}

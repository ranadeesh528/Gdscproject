package com.example.gdscproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


class sign_up : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var btnreg : Button
    private lateinit var signin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        emailInput = findViewById(R.id.createuser)
        passwordInput = findViewById(R.id.createpass)
        btnreg =findViewById(R.id.btn)
        signin = findViewById(R.id.tvSignUp)

        btnreg.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                createUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signin.setOnClickListener {
            val intent = Intent(this, sign_in::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI or navigate to the next screen
                    val intent = Intent(this, sign_in::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.message
                    val errorCode = (task.exception as? FirebaseAuthException)?.errorCode


                    // Print error details to logcat
                    Log.e("Registration", "Error Code: $errorCode, Message: $errorMessage")

                    // Show a toast with the error message
                    Toast.makeText(baseContext, "Registration failed. $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

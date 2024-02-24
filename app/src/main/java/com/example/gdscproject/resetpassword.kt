package com.example.gdscproject

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class resetpassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var passwordInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var btnsave : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        auth = FirebaseAuth.getInstance()
        passwordInput = findViewById(R.id.tietPassword)
        newPasswordInput = findViewById(R.id.tietPasswordNewPass)
        confirmPasswordInput = findViewById(R.id.tietPasswordConfirmNewPass)
        btnsave = findViewById(R.id.btnChangePassword)

        btnsave.setOnClickListener {
            val password = passwordInput.text.toString().trim()
            val newPassword = newPasswordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (newPassword == confirmPassword) {
                changePassword(password, newPassword)
            } else {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val user = auth.currentUser
        val credential = auth.currentUser?.email?.let { auth.signInWithEmailAndPassword(it, oldPassword) }

        credential?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user?.updatePassword(newPassword)
                    ?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Incorrect old password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
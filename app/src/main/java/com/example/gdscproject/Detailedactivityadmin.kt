package com.example.gdscproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gdscproject.databinding.ActivityDetailedactivityadminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Detailedactivityadmin : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedactivityadminBinding
    private var databaseReference: DatabaseReference? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null
    private var dataKey: String? = null
    private var datauser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedactivityadminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = auth.currentUser

        val bundle = intent.extras
        if(bundle != null){
            binding.adminnamefulldt.text = bundle.getString("fullname")
            binding.adminphno.text = bundle.getString("phonenumber")
            binding.adminpincodedt.text = bundle.getString("pincode")
            binding.adminstatedt.text = bundle.getString("state")
            binding.admincitydt.text = bundle.getString("city")
            binding.adminaddressdt.text = bundle.getString("address")
            val imgURL = bundle.getString("image")
            Glide.with(this).load(imgURL).into(binding.adminimagedt)

            // Retrieve the data key from the intent extras
            dataKey = bundle.getString("key")
            datauser = bundle.getString("user")
        }

        // Set an OnClickListener for the delete button
        binding.admindeltebtn.setOnClickListener {
            // Call the deleteData function to delete the data associated with this item
            deleteData()
        }


    }

    private fun deleteData() {
        // Add your delete data logic here
        // You can use the dataKey or datauser to identify the data to delete
        // For example:
        val userEmail = intent.getStringExtra("user")
        val dataKey = intent.getStringExtra("key")

        if (userEmail != null && dataKey != null) {
            // Construct the correct database reference
            databaseReference = FirebaseDatabase.getInstance().getReference("Clean Data").child(userEmail)
            databaseReference!!.child(dataKey).removeValue()
                .addOnSuccessListener {
                    // Data deleted successfully
                    Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(this, Administratorpage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Add FLAG_ACTIVITY_CLEAR_TOP flag
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    // Failed to delete data
                    Toast.makeText(
                        this,
                        "Failed to delete data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // Handle null values for userEmail or dataKey
            Toast.makeText(this, "Unable to delete data", Toast.LENGTH_SHORT).show()
        }
    }
}

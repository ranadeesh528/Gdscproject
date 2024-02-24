package com.example.gdscproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gdscproject.databinding.ActivityDetailedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

class DetailedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailedBinding
    private var databaseReference: DatabaseReference? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null
    private var dataKey: String? = null // Key of the data to be deleted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = auth.currentUser

        val bundle = intent.extras
        if(bundle != null){
            binding.namefulldt.text = bundle.getString("fullname")
            binding.phno.text = bundle.getString("phonenumber")
            binding.pincodedt.text = bundle.getString("pincode")
            binding.statedt.text = bundle.getString("state")
            binding.citydt.text = bundle.getString("city")
            binding.addressdt.text = bundle.getString("address")
            val imgURL = bundle.getString("image")
            Glide.with(this).load(imgURL).into(binding.imagedt)

            // Retrieve the data key from the intent extras
            dataKey = bundle.getString("key")
        }


    }



}
package com.example.gdscproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gdscproject.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class admin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var dataList: ArrayList<Dataclass>
    private lateinit var dataKeys: ArrayList<String>
    private lateinit var datauser: ArrayList<String>
    private lateinit var adapter: Myadapter
    private var databaseReference: DatabaseReference? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.process)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()
        dataKeys = ArrayList()
        datauser = ArrayList()
        adapter = Myadapter(this, dataList, dataKeys, datauser)
        binding.recyclerView.adapter = adapter

        val username = currentUser!!.email?.substringBefore('@')

        if (username != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Clean Data").child(username)
            databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    dataKeys.clear()
                    datauser.clear()
                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {
                            val dataClass = dataSnapshot.getValue(Dataclass::class.java)
                            dataClass?.let {
                                dataList.add(dataClass)
                                dataKeys.add(dataSnapshot.key!!)
                                // Assuming user is stored as a child node named "user"
                                val user = dataSnapshot.child("user").getValue(String::class.java)
                                datauser.add(user ?: "") // Add user to datauser list
                            }
                        }
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        val intent = Intent(this@admin, emptyactivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(this@admin, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@admin, "Username not found", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        val gridLayoutManager = GridLayoutManager(this, 1)
        binding.recyclerView.layoutManager = gridLayoutManager
    }
    override fun onBackPressed() {
        // Check if there are any activities in the back stack
        if (!isTaskRoot) {
            // If not at the root activity, navigate back
            super.onBackPressed()
        } else {
            // If at the root activity, navigate to the homepage activity
            val username = currentUser?.email?.substringBefore('@')
            if (username != null) {
                val intent = Intent(this, homepage::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            } else {
                // Username not found, handle accordingly
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
            }
        }
    }



}


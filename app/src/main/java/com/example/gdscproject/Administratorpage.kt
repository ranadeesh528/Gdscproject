package com.example.gdscproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gdscproject.databinding.ActivityAdministratorpageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Administratorpage : AppCompatActivity() {

    private lateinit var binding: ActivityAdministratorpageBinding
    private lateinit var dataList: ArrayList<Dataclass>
    private lateinit var dataKeys: ArrayList<String>
    private lateinit var datauser: ArrayList<String>
    private lateinit var adapter: Myadapteradmin
    private var databaseReference: DatabaseReference? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdministratorpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = auth.currentUser



        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.process)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()
        dataKeys = ArrayList()
        datauser = ArrayList()
        adapter = Myadapteradmin(this, dataList, dataKeys,datauser)
        binding.adminRecyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Clean Data")
        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                dataKeys.clear()
                datauser.clear() // Clear dataKeys and datauser before populating
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key // Get the user ID
                    for (dataSnapshot in userSnapshot.children) {
                        val dataClass = dataSnapshot.getValue(Dataclass::class.java)
                        dataClass?.let {
                            dataList.add(dataClass)
                            dataKeys.add(dataSnapshot.key!!) // Add data key to dataKeys
                            userId?.let { datauser.add(it) } // Add user ID to datauser
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
                Toast.makeText(this@Administratorpage, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val gridLayoutManager = GridLayoutManager(this, 1)
        binding.adminRecyclerView.layoutManager = gridLayoutManager
    }

    override fun onBackPressed() {
        // Check if there are any activities in the back stack
        if (!isTaskRoot) {
            // If not at the root activity, navigate back
            super.onBackPressed()
        } else {
            // If at the root activity, show a confirmation dialog
            AlertDialog.Builder(this)
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    // Perform logout actions
                    // For example, sign out the user and close the activity
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
                .setNegativeButton("No", null) // Do nothing if the user cancels
                .show()
        }
    }


}


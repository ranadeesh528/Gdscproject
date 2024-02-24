package com.example.gdscproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gdscproject.databinding.ActivityHomepageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class homepage : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var uploading: String? = null
    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            // User is not authenticated, navigate back to the login screen
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, sign_in::class.java))
            finish()
            return
        }

        val username = intent.getStringExtra("username")
        binding.userretrive.text = username // Set the username to the TextView

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    if (data.data != null) {
                        uri = data.data
                        binding.upload.setImageURI(uri)
                        setBorderToImageView(binding.upload)
                    } else {
                        val imageBitmap = data.extras?.get("data") as Bitmap?
                        if (imageBitmap != null) {
                            uri = getImageUri(imageBitmap)
                            val rotatedBitmap = rotateImageIfRequired(imageBitmap, uri!!)
                            binding.upload.setImageBitmap(rotatedBitmap)
                            setBorderToImageView(binding.upload)
                        }
                    }
                }
            }
        }

        binding.upload.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
                        } else {
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            if (takePictureIntent.resolveActivity(packageManager) != null) {
                                activityResultLauncher.launch(takePictureIntent)
                            } else {
                                Toast.makeText(this@homepage, "No camera app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    options[item] == "Choose from Gallery" -> {
                        val photoPicker = Intent(Intent.ACTION_PICK)
                        photoPicker.type = "image/*"
                        activityResultLauncher.launch(photoPicker)
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        binding.adddata.setOnClickListener {
            if (validateFields()) {
                savedata()
            }
        }

        binding.adminbtn.setOnClickListener {
            val intent = Intent(this@homepage, admin::class.java)
            startActivity(intent)
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        try {
            val input = contentResolver.openInputStream(uri)
            if (input != null) {
                val ei = ExifInterface(input)
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
                if (rotationAngle != 0f) {
                    return rotateBitmap(bitmap, rotationAngle)
                }
            } else {
                Toast.makeText(this@homepage, "Failed to open input stream", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this@homepage, "IOException: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }




    private fun setBorderToImageView(imageView: ImageView) {
        // Hardcoded border width in pixels
        val borderWidthPx = 4 // Adjust this value as needed

        // Create a ShapeDrawable with border color and thickness
        val shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.paint.color = Color.WHITE // Change this to your desired border color
        shapeDrawable.paint.style = Paint.Style.STROKE
        shapeDrawable.paint.strokeWidth = borderWidthPx.toFloat()

        // Set the background drawable with the border to the ImageView
        imageView.background = shapeDrawable
    }

    private fun validateFields(): Boolean {
        val fullName = binding.fullname.text.toString().trim()
        val phoneNumber = binding.phnumber.text.toString().trim()
        val pincode = binding.pincode.text.toString().trim()
        val state = binding.state.text.toString().trim()
        val city = binding.city.text.toString().trim()
        val address = binding.address.text.toString().trim()

        if (fullName.isEmpty() || phoneNumber.isEmpty() || pincode.isEmpty() ||
            state.isEmpty() || city.isEmpty() || address.isEmpty()) {
            Toast.makeText(this@homepage, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun savedata() {
        // Check if uri is not null
        uri ?: run {
            Toast.makeText(this@homepage, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference.child("Task Image")
            .child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@homepage)
        builder.setCancelable(false)
        builder.setView(R.layout.process)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener { urlTask ->
                if (urlTask.isSuccessful) {
                    val urlImage = urlTask.result

                    uploading = urlImage.toString()
                    uploaddata()
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    Toast.makeText(this@homepage, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            dialog.dismiss()
            // Handle specific exception for file upload failure
            Toast.makeText(this@homepage, "Failed to upload file: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploaddata() {
        val username = intent.getStringExtra("username") ?: return // Retrieve username passed from sign-in activity
        val name = binding.fullname.text.toString()
        val phoneNumber = binding.phnumber.text.toString()
        val pincode = binding.pincode.text.toString()
        val state = binding.state.text.toString()
        val city = binding.city.text.toString()
        val address = binding.address.text.toString()

        val dataclass = Dataclass(name, phoneNumber, pincode, state, city, address, uploading)

        val databaseReference = FirebaseDatabase.getInstance().getReference("Clean Data").child(username)
        val entryKey = databaseReference.push().key // Generate unique key for each entry

        if (entryKey != null) {
            databaseReference.child(entryKey).setValue(dataclass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@homepage, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@homepage, admin::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@homepage, "Failed to save data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@homepage, "Failed to generate entry key", Toast.LENGTH_SHORT).show()
        }
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

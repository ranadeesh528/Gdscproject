package com.example.gdscproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the image view
        image = findViewById(R.id.logo)

        // Set the initial alpha to 0
        image.alpha = 0f

        // Create an animation to fade in the image
        image.animate().apply {
            duration = 1500 // Set the duration of the animation (1.5 seconds)
            alpha(1f)        // Set the target alpha (1f means fully opaque)
            withEndAction {
                // Code to execute when the animation ends
                val i = Intent(this@MainActivity, sign_in::class.java)
                startActivity(i)

                // Optional: Finish the current activity if you don't want to go back to it
                finish()
            }
        }
    }
}

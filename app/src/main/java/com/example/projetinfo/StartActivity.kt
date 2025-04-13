package com.example.projetinfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Charge le layout de démarrage (activity_start.xml)
        setContentView(R.layout.activity_start)

        val playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener {
            // Lance MainActivity en passant le niveau (1 par défaut)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("level", 1)
            startActivity(intent)
        }
    }
}

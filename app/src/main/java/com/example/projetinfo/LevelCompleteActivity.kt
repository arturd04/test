package com.example.projetinfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LevelCompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_complete)

        // Récupérez le niveau suivant depuis l'Intent, si besoin
        val nextLevel = intent.getIntExtra("nextLevel", 1)

        // On va afficher le score total dans le ScoreManager,
        // qui contient le cumul des aliens tués sur tous les niveaux.
        val totalScore = ScoreManager.totalScore

        // Récupération du TextView et mise à jour du texte
        val messageTextView = findViewById<TextView>(R.id.messageTextView)
        messageTextView.text = "Bravo !\nScore: $totalScore\nProchain niveau: $nextLevel"

        // Bouton pour passer au niveau suivant
        val nextLevelButton = findViewById<Button>(R.id.nextLevelButton)
        nextLevelButton.setOnClickListener {
            // Lancer MainActivity pour le niveau suivant
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("level", nextLevel)
            startActivity(intent)
            finish()
        }
    }
}

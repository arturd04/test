package com.example.projetinfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity(), ScoreObserver {

    private lateinit var gameOverTextView: TextView
    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        gameOverTextView = findViewById(R.id.gameOverTextView)
        restartButton = findViewById(R.id.restartButton)

        // S'inscrire comme observateur du score
        ScoreManager.addObserver(this)
        // Afficher le score global initial
        onScoreChanged(ScoreManager.totalScore)

        restartButton.setOnClickListener {
            // Réinitialiser le score global
            ScoreManager.reset()
            // Créer un Intent vers MainActivity avec le niveau 1
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("level", 1)
            // Définir les flags pour effacer la pile d'activités et redémarrer MainActivity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Se désinscrire pour éviter toute fuite mémoire
        ScoreManager.removeObserver(this)
    }

    // Implémentation de l'interface ScoreObserver
    override fun onScoreChanged(newScore: Int) {
        gameOverTextView.text = "Game Over\nScore Total: $newScore"
    }
}

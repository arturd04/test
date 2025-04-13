package com.example.projetinfo

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Charge le layout défini dans activity_main.xml
        setContentView(R.layout.activity_main)

        // Récupère le niveau depuis l'Intent (1 par défaut)
        val level = intent.getIntExtra("level", 1)

        // Mise à jour du TextView affichant le niveau
        val levelTextView = findViewById<TextView>(R.id.levelTextView)
        levelTextView.text = "Niveau : $level"

        // Récupère le conteneur pour le GameView et ajoute-la
        val container = findViewById<FrameLayout>(R.id.gameContainer)
        gameView = GameView(this)
        gameView.setLevel(level)
        container.addView(gameView)

        // Configure le callback pour mettre à jour l'affichage des HP du joueur
        val playerHpTextView = findViewById<TextView>(R.id.playerHpTextView)
        gameView.onPlayerHpChanged = { hp ->
            runOnUiThread {
                playerHpTextView.text = "HP : $hp"
            }
        }
    }
}

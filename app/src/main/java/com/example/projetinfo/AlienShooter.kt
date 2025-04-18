package com.example.projetinfo

import android.content.Context

class AlienShooter(context: Context, x: Float, y: Float) : Alien(context, x, y) {

    init {
        // Ce type d'alien tire plus souvent (toutes les 1 à 3 secondes)
        nextShotDelay = (1000..3000).random().toLong()
    }

}


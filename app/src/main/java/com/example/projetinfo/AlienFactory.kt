package com.example.projetinfo

import android.content.Context

object AlienFactory {
    /**
     * Crée un alien en fonction du niveau.

     * @param context Le contexte pour la création de ressources.
     * @param x La coordonnée x de l'alien.
     * @param y La coordonnée y de l'alien.
     * @param currentLevel Le niveau actuel du jeu.
     * @return Un objet Alien ou AlienWithHP selon la logique définie.
     */
    fun createAlien(context: Context, x: Float, y: Float, currentLevel: Int): Alien {
        return if (currentLevel == 1) {
            // Au niveau 1, tous les aliens sont basiques (1 hp)
            Alien(context, x, y)
        } else {
            when ((1..3).random()) {
                1 -> Alien(context, x, y)  // Alien basique (1 hp)
                2 -> AlienWithHP(context, x, y, initialHp = 2)  // Alien avec 2 hp
                else -> AlienShooter(context, x, y) // Alien avec 3 hp
            }
        }
    }
}

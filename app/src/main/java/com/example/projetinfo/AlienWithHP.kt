package com.example.projetinfo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

class AlienWithHP(
    context: Context,
    x: Float,
    y: Float,
    initialHp: Int = 2
) : Alien(context, x, y), Damageable {

    // La propriété hp est maintenant override avec un setter public (comme requis par l'interface)
    override var hp: Int = initialHp

    override fun draw(canvas: Canvas) {
        // Appelle le dessin de base (utilise le bitmap protégé de la classe Alien)
        super.draw(canvas)
        // Affiche le nombre de points de vie si hp > 1
        if (hp > 1) {
            val textPaint = Paint().apply {
                color = Color.BLUE
                textSize = 46f
                // Affiche le texte en gras
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val text = "$hp"
            val textWidth = textPaint.measureText(text)
            val xPos = x + width / 2 - textWidth / 2
            val yPos = y + height / 2 + textPaint.textSize / 2
            canvas.drawText(text, xPos, yPos, textPaint)
        }
    }

    override fun applyDamage(damage: Int): Boolean {
        hp -= damage
        return hp <= 0
    }
}

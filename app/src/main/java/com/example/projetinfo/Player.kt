package com.example.projetinfo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

class Player(context: Context) {
    var x = 100f
    var y = 0f
    var width: Float
    var height: Float
    var hp: Int = 3  // Le joueur a 3 points de vie par partie
    private val bitmap: Bitmap

    init {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.player)
        // Nouvelle taille souhaitée pour le joueur
        val newWidth = 200
        val newHeight = 200
        bitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        // Positionnement en bas de l'écran avec un léger marge de 50dp
        y = context.resources.displayMetrics.heightPixels - height - 50
    }

    fun draw(canvas: Canvas) {
        // Dessine l'image du joueur centrée horizontalement
        canvas.drawBitmap(bitmap, x - width / 2, y, null)
    }

    // Méthode pour obtenir le rectangle de collision du joueur
    fun getRect(): RectF {
        return RectF(x - width / 2, y, x + width / 2, y + height)
    }
}

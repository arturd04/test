package com.example.projetinfo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

open class Alien(context: Context, var x: Float, var y: Float) {
    // Déclare le bitmap en protected pour que les classes héritées puissent y accéder
    protected val bitmap: Bitmap
    val width: Float
    val height: Float

    init {
        val original = BitmapFactory.decodeResource(context.resources, R.drawable.alien)
        bitmap = Bitmap.createScaledBitmap(original, 100, 100, true)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
    }

    open fun draw(canvas: Canvas) {
        // Utilise le bitmap protégé pour dessiner l'alien
        canvas.drawBitmap(bitmap, x, y, null)
    }

    fun getRect(): RectF {
        return RectF(x, y, x + width, y + height)
    }
}

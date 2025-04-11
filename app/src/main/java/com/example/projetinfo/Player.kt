package com.example.projetinfo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas

class Player(context: Context) {
    var x = 100f
    var y = 0f
    var width: Float
    var height: Float
    private val bitmap: Bitmap

    init {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.player)
        val newWidth = 200
        val newHeight = 200
        bitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        y = context.resources.displayMetrics.heightPixels - height - 50
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x - width / 2, y, null)
    }
}
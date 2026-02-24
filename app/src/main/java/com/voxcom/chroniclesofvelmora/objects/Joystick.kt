package com.voxcom.chroniclesofvelmora.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.*

class Joystick(
    private val centerX: Float,
    private val centerY: Float,
    private val baseRadius: Float,
    private val hatRadius: Float
) {

    private val basePaint = Paint().apply { color = Color.DKGRAY }
    private val hatPaint = Paint().apply { color = Color.LTGRAY }

    private var actuatorX = 0f
    private var actuatorY = 0f

    fun draw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint)
        canvas.drawCircle(
            centerX + actuatorX * baseRadius,
            centerY + actuatorY * baseRadius,
            hatRadius,
            hatPaint
        )
    }

    fun setActuator(touchX: Float, touchY: Float) {
        val deltaX = touchX - centerX
        val deltaY = touchY - centerY
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

        if (distance < baseRadius) {
            actuatorX = deltaX / baseRadius
            actuatorY = deltaY / baseRadius
        } else {
            actuatorX = deltaX / distance
            actuatorY = deltaY / distance
        }
    }

    fun resetActuator() {
        actuatorX = 0f
        actuatorY = 0f
    }

    fun getActuatorX(): Float = actuatorX
    fun getActuatorY(): Float = actuatorY
}
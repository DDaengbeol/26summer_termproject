package com.example.excerption.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.excerption.R
import kotlin.math.max

class RatingDistributionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val entries = mutableListOf<Pair<String, Float>>()
    private var selectedIndex: Int? = null

    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(103, 116, 149)
        style = Paint.Style.FILL
    }
    private val grayOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(181, 183, 188)
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.text_primary)
        textSize = 18f
        textAlign = Paint.Align.CENTER
    }
    private val barRect = RectF()

    fun submitEntries(newEntries: List<Pair<String, Float>>) {
        entries.clear()
        entries.addAll(newEntries)
        selectedIndex = selectedIndex?.takeIf { it in entries.indices }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP || entries.isEmpty()) return true
        val index = hitIndex(event.x)
        selectedIndex = index
        invalidate()
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (entries.isEmpty()) return

        val maxValue = max(1f, entries.maxOf { it.second })
        val left = paddingLeft.toFloat()
        val right = width - paddingRight.toFloat()
        val chartTop = paddingTop + 20f
        val chartBottom = height - paddingBottom.toFloat()
        val slotWidth = (right - left) / entries.size
        val barWidth = slotWidth * 0.72f

        entries.forEachIndexed { index, entry ->
            val value = entry.second
            val centerX = left + slotWidth * index + slotWidth / 2f
            val barTop = if (value > 0f) {
                val calculatedTop = chartBottom - (chartBottom - chartTop) * (value / maxValue)
                barRect.set(centerX - barWidth / 2f, calculatedTop, centerX + barWidth / 2f, chartBottom)
                val isBlueBar = index % 2 == 0
                if (isBlueBar) {
                    canvas.drawRect(barRect, bluePaint)
                } else {
                    canvas.drawRect(barRect, grayOutlinePaint)
                }
                calculatedTop
            } else {
                chartBottom
            }
            if (selectedIndex == index) {
                canvas.drawText("${value.toInt()}권", centerX, barTop - 8f, valuePaint)
            }
        }
    }

    private fun hitIndex(x: Float): Int? {
        if (entries.isEmpty()) return null
        val left = paddingLeft.toFloat()
        val right = width - paddingRight.toFloat()
        if (x < left || x > right) return null
        val slotWidth = (right - left) / entries.size
        return ((x - left) / slotWidth).toInt().coerceIn(0, entries.lastIndex)
    }

}

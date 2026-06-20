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

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val entries = mutableListOf<Pair<String, Float>>()
    private var selectedIndex: Int? = null

    private val selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(103, 116, 149)
        style = Paint.Style.FILL
    }
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(181, 183, 188)
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.text_primary)
        textSize = 26f
        textAlign = Paint.Align.CENTER
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
        selectedIndex = selectedIndex?.takeIf { it in entries.indices && entries[it].second > 0f }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP || entries.isEmpty()) return true
        val index = hitIndex(event.x)
        selectedIndex = index?.takeIf { entries[it].second > 0f }
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

        val visibleEntries = entries.take(12)
        val maxValue = max(1f, visibleEntries.maxOf { it.second })
        val left = paddingLeft.toFloat()
        val right = width - paddingRight.toFloat()
        val chartTop = paddingTop + 24f
        val labelBaseline = height - paddingBottom - 4f
        val chartBottom = labelBaseline - 34f
        val slotWidth = (right - left) / visibleEntries.size
        val barWidth = slotWidth * 0.58f

        visibleEntries.forEachIndexed { index, entry ->
            val value = entry.second
            val centerX = left + slotWidth * index + slotWidth / 2f
            if (value > 0f) {
                val barTop = chartBottom - (chartBottom - chartTop) * (value / maxValue)
                barRect.set(centerX - barWidth / 2f, barTop, centerX + barWidth / 2f, chartBottom)
                if (selectedIndex == index) {
                    canvas.drawRect(barRect, selectedPaint)
                    canvas.drawText("${value.toInt()}권", centerX, barTop - 8f, valuePaint)
                } else {
                    canvas.drawRect(barRect, outlinePaint)
                }
            }
            canvas.drawText(entry.first, centerX, labelBaseline, labelPaint)
        }
    }

    private fun hitIndex(x: Float): Int? {
        val visibleCount = minOf(entries.size, 12)
        if (visibleCount == 0) return null
        val left = paddingLeft.toFloat()
        val right = width - paddingRight.toFloat()
        if (x < left || x > right) return null
        val slotWidth = (right - left) / visibleCount
        return ((x - left) / slotWidth).toInt().coerceIn(0, visibleCount - 1)
    }
}

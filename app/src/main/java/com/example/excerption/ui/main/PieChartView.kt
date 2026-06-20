package com.example.excerption.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.excerption.R

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val entries = mutableListOf<Pair<String, Float>>()
    private val palette = intArrayOf(
        Color.rgb(49, 92, 72),
        Color.rgb(196, 106, 58),
        Color.rgb(86, 128, 163),
        Color.rgb(141, 107, 165),
        Color.rgb(222, 181, 77)
    )
    private val oval = RectF()
    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.text_secondary)
        textSize = 30f
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.text_primary)
        textSize = 34f
        isFakeBoldText = true
    }

    fun submitEntries(newEntries: List<Pair<String, Float>>) {
        entries.clear()
        entries.addAll(newEntries.filter { it.second > 0f })
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (entries.isEmpty()) {
            canvas.drawText("기록이 없습니다.", paddingLeft.toFloat(), height / 2f, titlePaint)
            return
        }

        val total = entries.sumOf { it.second.toDouble() }.toFloat()
        val chartSize = (height - paddingTop - paddingBottom).coerceAtMost(width / 2)
        val left = paddingLeft.toFloat()
        val top = paddingTop + (height - paddingTop - paddingBottom - chartSize) / 2f
        oval.set(left, top, left + chartSize, top + chartSize)
        var startAngle = -90f

        entries.forEachIndexed { index, entry ->
            slicePaint.color = palette[index % palette.size]
            val sweep = 360f * (entry.second / total)
            canvas.drawArc(oval, startAngle, sweep, true, slicePaint)
            startAngle += sweep
        }

        val legendLeft = oval.right + 28f
        var legendY = top + 30f
        entries.forEachIndexed { index, entry ->
            slicePaint.color = palette[index % palette.size]
            canvas.drawCircle(legendLeft, legendY - 10f, 10f, slicePaint)
            val percent = (entry.second / total * 100).toInt()
            canvas.drawText(entry.first.take(8), legendLeft + 22f, legendY, titlePaint)
            canvas.drawText("${entry.second.toInt()}권 · $percent%", legendLeft + 22f, legendY + 34f, textPaint)
            legendY += 72f
        }
    }
}

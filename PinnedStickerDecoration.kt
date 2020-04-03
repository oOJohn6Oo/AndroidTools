package com.github.john.musecam

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.min

class PinnedStickerDecoration(context: Context,private val tests: MutableList<String>) : RecyclerView.ItemDecoration() {
    private val textPaint = TextPaint()
    private val bgdPaint = Paint()
    private val dp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1f,context.resources.displayMetrics)

    init {
        textPaint.apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, android.R.color.darker_gray)
            typeface = Typeface.DEFAULT_BOLD
            textSize = dp1 * 12
        }
        bgdPaint.color = ContextCompat.getColor(context, R.color.divide_color)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (pos == 0 || tests[pos][0].toUpperCase() != tests[pos - 1][0].toUpperCase()) {
            outRect.top = (dp1 * 24).toInt()
        } else {
            outRect.top = dp1.toInt()
        }

    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        for (i: Int in 0 until parent.childCount) {
            val view = parent[i]
            val pos = parent.getChildAdapterPosition(view)
            if (view.top > dp1 * 24) {
                // 第一个
                if (pos == 0 || tests[pos][0].toUpperCase() != tests[pos - 1][0].toUpperCase()) {
                    // 顶部显示
                    c.drawRect(
                        view.left.toFloat(),
                        view.top - dp1 * 24,
                        view.right.toFloat(),
                        view.top.toFloat(),
                        bgdPaint
                    )
                    c.drawText(
                        tests[pos][0].toString().toUpperCase(Locale.ENGLISH),
                        view.left.toFloat() + dp1 * 16,
                        view.top - dp1 * 6,
                        textPaint
                    )
                } else {
                    // 分隔线
                    c.drawRect(
                        view.left.toFloat(),
                        view.top - dp1,
                        view.right.toFloat(),
                        view.top.toFloat(),
                        bgdPaint
                    )
                }
            } else {
                // 最后一个
                if (pos + 1 < tests.size && tests[pos][0].toString().toUpperCase(Locale.ENGLISH) != tests[pos + 1][0].toString().toUpperCase(
                        Locale.ENGLISH
                    )
                ) {

                    // 拖动
                    val stickY: Float = min(view.bottom.toFloat(), dp1 * 24)
                    c.drawRect(
                        view.left.toFloat(),
                        stickY - dp1 * 24,
                        view.right.toFloat(),
                        stickY,
                        bgdPaint
                    )
                    c.drawText(
                        tests[pos][0].toString().toUpperCase(Locale.ENGLISH),
                        view.left.toFloat() + dp1 * 16,
                        stickY - dp1 * 6,
                        textPaint
                    )
                } else {
                    // 置顶
                    c.drawRect(
                        view.left.toFloat(),
                        0f,
                        view.right.toFloat(),
                        dp1 * 24,
                        bgdPaint
                    )
                    c.drawText(
                        tests[pos][0].toString().toUpperCase(Locale.ENGLISH),
                        view.left.toFloat() + dp1 * 16,
                        dp1 * 18,
                        textPaint
                    )
                }
            }
        }

    }
}
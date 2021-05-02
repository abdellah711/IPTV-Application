package com.app.tvapp.others

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.app.tvapp.R
import kotlin.math.min

class CoolProgressbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    var progress = 1f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        ValueAnimator.ofFloat(.2f,.9f).apply {
            duration = 1000
            repeatCount = INFINITE
            repeatMode = REVERSE
            addUpdateListener {
                progress = it.animatedValue as Float

                alpha = progress - .15f

                invalidate()
            }
        }.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(
            width/2f,
            height/2f,
            progress*width/2f,
            Paint().apply {
                color = context.getColor(R.color.purple_200)
            }
        )

    }



}
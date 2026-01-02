package com.liuziqi.a202305100203.foods.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.liuziqi.a202305100203.foods.R

class RouletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f

    private val foodItems = listOf(
        FoodItem("火锅", Color.RED),
        FoodItem("烧烤", Color.parseColor("#FF9800")),
        FoodItem("日料", Color.parseColor("#E91E63")),
        FoodItem("川菜", Color.parseColor("#4CAF50")),
        FoodItem("西餐", Color.parseColor("#2196F3")),
        FoodItem("小吃", Color.parseColor("#9C27B0"))
    )

    private var rotateAngle = 0f
    private var isSpinning = false

    data class FoodItem(val name: String, val color: Int)

    init {
        setupPaint()
    }

    private fun setupPaint() {
        // 扇形画笔
        paint.style = Paint.Style.FILL

        // 文字画笔
        textPaint.color = Color.WHITE
        textPaint.textSize = 36f
        textPaint.textAlign = Paint.Align.CENTER

        // 中心圆画笔
        centerPaint.color = ContextCompat.getColor(context, R.color.white)
        centerPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        radius = (minOf(w, h) / 2 * 0.9).toFloat()
        centerX = w / 2f
        centerY = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.rotate(rotateAngle, centerX, centerY)

        // 绘制扇形
        val sweepAngle = 360f / foodItems.size
        var startAngle = -90f // 从顶部开始

        for (i in foodItems.indices) {
            paint.color = foodItems[i].color
            val rectF = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)

            // 绘制文字
            val angle = startAngle + sweepAngle / 2
            val textRadius = radius * 0.6f
            val x = centerX + textRadius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = centerY + textRadius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()

            canvas.save()
            canvas.rotate(angle + 90, x, y)
            canvas.drawText(foodItems[i].name, x, y, textPaint)
            canvas.restore()

            startAngle += sweepAngle
        }

        canvas.restore()

        // 绘制中心圆
        canvas.drawCircle(centerX, centerY, radius * 0.1f, centerPaint)
    }

    fun spin(selectedIndex: Int = -1) {
        if (isSpinning) return

        isSpinning = true

        // 计算旋转角度
        val sweepAngle = 360f / foodItems.size
        val targetAngle = if (selectedIndex == -1) {
            // 随机选择一个
            val randomIndex = (0 until foodItems.size).random()
            360f * 5 + (360 - sweepAngle * randomIndex) // 多转5圈后停在选中的位置
        } else {
            360f * 5 + (360 - sweepAngle * selectedIndex)
        }

        // 创建动画
        val animator = ValueAnimator.ofFloat(0f, targetAngle)
        animator.duration = 3000
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            rotateAngle = animation.animatedValue as Float
            invalidate()
        }

        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {
                isSpinning = true
            }

            override fun onAnimationEnd(animation: android.animation.Animator) {
                isSpinning = false
                rotateAngle %= 360
                invalidate()
                onSpinCompleteListener?.invoke(getCurrentFood())
            }

            override fun  onAnimationCancel(animation: android.animation.Animator) {}
            override fun  onAnimationRepeat(animation: android.animation.Animator) {}
        })

        animator.start()
    }

    private fun getCurrentFood(): String {
        val currentAngle = (rotateAngle % 360 + 360) % 360
        val sweepAngle = 360f / foodItems.size
        val normalizedAngle = (currentAngle + 90) % 360 // 调整起始位置

        val index = ((normalizedAngle / sweepAngle).toInt() % foodItems.size)
        return foodItems[foodItems.size - 1 - index].name // 反向计算
    }

    private var onSpinCompleteListener: ((String) -> Unit)? = null

    fun setOnSpinCompleteListener(listener: (String) -> Unit) {
        this.onSpinCompleteListener = listener
    }
}
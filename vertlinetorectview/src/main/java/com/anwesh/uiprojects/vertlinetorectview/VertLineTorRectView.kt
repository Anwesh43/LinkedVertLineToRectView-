package com.anwesh.uiprojects.vertlinetorectview

/**
 * Created by anweshmishra on 09/02/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log

val nodes : Int = 5
val lines : Int = 2
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val delay : Long = 20
val foreColor : Int = Color.parseColor("#673AB7")
val backColor : Int = Color.parseColor("#212121")
val hFactor : Float = 1f
val wFactor : Float = 0.5f
val sizeFactor : Float = 2.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), this.maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

//fun main() {
//    //Math.min(1f / 2, 0.3f.maxScale(0, 2)) * 2
//    Math.min(0.5f, Math.max(0f, 0.3f - 0 * 0.5f)) * 2
//
//    Math.min(0.5f, Math.max(0f, 0.3f)) * 2
//    Math.min(0.5f, 0.3f) * 2
//    0.3f * 2 -> 0.6
//}

fun Canvas.drawVertRect(scale : Float, size : Float, paint : Paint) {
    val rw : Float = size / wFactor
    val rh : Float = size / hFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, 2)
    val sf2 : Float = sf.divideScale(1, 2)
    val path : Path = Path()
    val x1 : Float = -rw / 2
    val x2 : Float = -rw / 2 + rw * sf1
    val x3 : Float = -rw / 2 + rw * sf2
    val y1 : Float = -rh / 2
    val y2 : Float = rh / 2
    paint.style = Paint.Style.STROKE
    path.moveTo(x1, y2)
    path.lineTo(x1, y1)
    path.lineTo(x2, y1)
    path.lineTo(x3, y2)
    path.lineTo(x1, y2)
    drawPath(path, paint)
}

fun Canvas.drawVLTRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawVertRect(scale, size, paint)
    restore()
}

class VertLineToRectView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val animator : Animator = Animator(this)
    private val vlt : VertLineToRect = VertLineToRect(0)

    override fun onDraw(canvas : Canvas) {
        canvas.drawColor(backColor)
        vlt.draw(canvas, paint)
        animator.animate {
            vlt.update {
                animator.stop()
            }
        }

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
               vlt.startUpdating {
                   animator.start()
               }
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : () -> Unit) {
            scale += dir * scGap
            Log.d("scale is", "${scale}")
            if (scale > 1f) {
                dir = 0f
                cb()
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f
                scale = 0f
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                Log.d("animating", "${SystemClock.currentThreadTimeMillis()}")
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                Log.d("started Animation", "${SystemClock.currentThreadTimeMillis()}")
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                Log.d("stopping Animation", "${SystemClock.currentThreadTimeMillis()}")
                animated = false
            }
        }
    }

    data class VLTNode(var i : Int, val state : State = State()) {

        private var next : VLTNode? = null
        private var prev : VLTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = VLTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawVLTRNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : () -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : VLTNode {
            var curr : VLTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class VertLineToRect(var i : Int) {

        private var root : VLTNode = VLTNode(0)
        private var curr : VLTNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun  update(cb : () -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb()

            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    companion object {

        fun create(activity : Activity) : VertLineToRectView {
            val view : VertLineToRectView = VertLineToRectView(activity)
            activity.setContentView(view)
            return view
        }
    }
}

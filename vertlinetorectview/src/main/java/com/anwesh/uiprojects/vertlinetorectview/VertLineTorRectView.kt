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

val nodes : Int = 5
val lines : Int = 2
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val delay : Long = 20
val foreColor : Int = Color.parseColor("#673AB7")
val backColor : Int = Color.parseColor("#BDBDBD")

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


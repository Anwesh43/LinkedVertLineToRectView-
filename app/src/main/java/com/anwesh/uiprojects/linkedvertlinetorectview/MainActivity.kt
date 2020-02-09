package com.anwesh.uiprojects.linkedvertlinetorectview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.vertlinetorectview.VertLineToRectView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VertLineToRectView.create(this)
    }
}

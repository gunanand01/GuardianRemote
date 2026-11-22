package com.guardian.remote

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Guardian Remote\nApp Started Successfully!"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
        }

        setContentView(textView)
    }
}

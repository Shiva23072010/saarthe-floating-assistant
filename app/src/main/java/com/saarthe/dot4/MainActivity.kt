package com.saarthe.dot4

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.rgb(7,9,13))
        }
        val title = TextView(this).apply {
            text = "Saarthe"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        val info = TextView(this).apply {
            text = "Floating translation assistant\\n\\n1. Enable Saarthe Accessibility Service\\n2. Allow Display over other apps\\n3. Open WhatsApp/Messenger\\n4. Saarthe will show translation and suggested replies for visible chat text.\\n\\nYou control these permissions and can turn them off anytime."
            textSize = 15f
            setTextColor(Color.LTGRAY)
            setPadding(0, 25, 0, 25)
        }
        val accessibility = Button(this).apply {
            text = "Enable Accessibility Service"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        val overlay = Button(this).apply {
            text = "Allow Floating Window"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            }
        }
        root.addView(title)
        root.addView(info)
        root.addView(accessibility)
        root.addView(overlay)
        setContentView(root)
    }
}

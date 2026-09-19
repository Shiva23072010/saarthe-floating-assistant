package com.saarthe.dot4

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import java.util.Locale

class SaartheOverlay(private val context: Context) {
    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var view: LinearLayout? = null
    private var lastMessage = ""

    fun show(message: String, translation: String, suggestions: List<String>, replyLanguage: String) {
        hide()
        lastMessage = message

        val box = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 18, 24, 18)
            setBackgroundColor(Color.rgb(15, 20, 30))
        }

        fun tv(text: String, size: Float, bold: Boolean = false): TextView =
            TextView(context).apply {
                this.text = text
                setTextColor(Color.WHITE)
                textSize = size
                if (bold) typeface = Typeface.DEFAULT_BOLD
                setPadding(0, 5, 0, 5)
            }

        box.addView(tv("Saarthe  •  Floating Assistant", 17f, true))
        box.addView(tv("Incoming message", 11f, true))
        box.addView(tv(message, 14f))
        box.addView(tv("Translation", 11f, true))
        box.addView(tv(translation, 15f, true))

        val suggestTitle = tv("Contextual reply suggestions", 11f, true)
        box.addView(suggestTitle)

        // Declare before suggestion buttons so their click listeners can safely update it.
        val reply = EditText(context).apply {
            hint = "Type your reply..."
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setSingleLine(false)
            minLines = 2
            maxLines = 4
        }
        box.addView(tv("Your reply (${replyLanguage.uppercase(Locale.US)})", 11f, true))
        box.addView(reply, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        suggestions.take(3).forEach { suggestion ->
            val b = Button(context).apply {
                text = suggestion
                setAllCaps(false)
                setOnClickListener {
                    reply.setText(suggestion)
                    reply.setSelection(reply.text.length)
                    reply.requestFocus()
                }
            }
            box.addView(b, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val row = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
        fun action(label: String, onClick: () -> Unit): Button =
            Button(context).apply {
                text = label
                setAllCaps(false)
                setOnClickListener { onClick() }
            }

        row.addView(action("Translate reply") {
            val text = reply.text.toString()
            if (text.isNotBlank()) {
                val target = if (replyLanguage == "hi") "en" else "hi"
                TranslationEngine(context).translate(text, replyLanguage, target) { translated ->
                    reply.post { reply.setText(translated) }
                }
            }
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        row.addView(action("DOT4") {
            val text = reply.text.toString()
            if (text.isNotBlank()) reply.setText(Dot4Codec.encode(text))
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        row.addView(action("Copy") {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Saarthe reply", reply.text.toString()))
            Toast.makeText(context, "Reply copied", Toast.LENGTH_SHORT).show()
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        box.addView(row)

        val share = Button(context).apply {
            text = "Share reply"
            setAllCaps(false)
            setOnClickListener {
                context.startActivity(Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, reply.text.toString())
                    }, "Share reply"
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
        box.addView(share)

        val close = Button(context).apply {
            text = "Close"
            setAllCaps(false)
            setOnClickListener { hide() }
        }
        box.addView(close)

        val type = if (android.os.Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
            y = 60
        }
        wm.addView(box, params)
        view = box
    }

    fun hide() {
        view?.let { runCatching { wm.removeView(it) } }
        view = null
    }
}

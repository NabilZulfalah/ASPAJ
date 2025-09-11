package com.example.aspaj.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class Typewriter(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    private var mText: CharSequence = ""
    private var mIndex = 0
    private var mDelay: Long = 100 // delay antar huruf (ms)

    private val handler = Handler(Looper.getMainLooper())

    private val characterAdder = object : Runnable {
        override fun run() {
            text = mText.subSequence(0, mIndex++)
            if (mIndex <= mText.length) {
                handler.postDelayed(this, mDelay)
            }
        }
    }

    fun animateText(txt: CharSequence) {
        mText = txt
        mIndex = 0
        text = ""
        handler.removeCallbacks(characterAdder)
        handler.postDelayed(characterAdder, mDelay)
    }

    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }
}

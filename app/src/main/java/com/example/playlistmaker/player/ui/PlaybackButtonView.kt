package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    ) : View(context, attrs, defStyleAttr, defStyleRes){

    private var isPlaying = false
    private val imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?
    private val imageRect = RectF(0F, 0F, 0F, 0F)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imagePauseBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseImageId)?.toBitmap()
                imagePlayBitmap = getDrawable(R.styleable.PlaybackButtonView_playImageId)?.toBitmap()
            }
            finally{
                recycle()
            }}

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0F, 0F, measuredWidth.toFloat(), measuredWidth.toFloat())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
           MotionEvent.ACTION_DOWN -> {
               return true
           }
           MotionEvent.ACTION_UP -> {
               switchState()
               super.callOnClick()
               return true
           }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        if (isPlaying){
            drawImage(canvas,imagePauseBitmap)
        } else {
            drawImage(canvas, imagePlayBitmap)
        }
    }

    fun switchState(){
        isPlaying = !isPlaying
        invalidate()
    }

    fun switchState(newIsPlaying: Boolean){
        isPlaying = newIsPlaying
        invalidate()
    }

    private fun drawImage(canvas: Canvas, imageBitmap: Bitmap?){
        imageBitmap?.let {
           canvas.drawBitmap(imageBitmap, null, imageRect, null)
        }
    }

}

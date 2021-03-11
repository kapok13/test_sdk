package com.hwpty.halloweenparty.presentation.listener

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.hwpty.halloweenparty.presentation.activity.PuzzleActivity
import com.hwpty.halloweenparty.presentation.manager.Piece
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PuzzleListener(private val puzzleActivity: PuzzleActivity) : View.OnTouchListener {


    private var xDelta = 0f
    private var yDelta = 0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val x: Float = event!!.rawX
        val y: Float = event.rawY
        val tolerance = sqrt(
            v!!.width.toDouble().pow(2.0) + v.height.toDouble().pow(2.0)
        ) / 10

        val piece: Piece = v as Piece
        if (!piece.canMove) {
            return true
        }

        val lParams: RelativeLayout.LayoutParams =
            v.getLayoutParams() as RelativeLayout.LayoutParams
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = x - lParams.leftMargin
                yDelta = y - lParams.topMargin
                piece.bringToFront()
            }
            MotionEvent.ACTION_MOVE -> {
                lParams.leftMargin = (x - xDelta).toInt()
                lParams.topMargin = (y - yDelta).toInt()
                v.setLayoutParams(lParams)
            }
            MotionEvent.ACTION_UP -> {
                val xDiff: Int = abs(piece.xCord - lParams.leftMargin)
                val yDiff: Int = abs(piece.yCord - lParams.topMargin)
                if (xDiff <= tolerance && yDiff <= tolerance) {
                    lParams.leftMargin = piece.xCord
                    lParams.topMargin = piece.yCord
                    piece.layoutParams = lParams
                    piece.canMove = false
                    sendViewToBack(piece)
                    puzzleActivity.checkGameOver()
                }
            }
        }

        return true
    }

    private fun sendViewToBack(child: View) {
        val parent = child.parent as ViewGroup
        parent.removeView(child)
        parent.addView(child, 0)
    }

}
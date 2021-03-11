package com.hwpty.halloweenparty.presentation.manager

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView

class Piece(
    pieceContext: Context
) : AppCompatImageView(pieceContext) {

    var xCord: Int = 0
    var yCord: Int = 0
    var pieceWidth: Int = 0
    var pieceHeight: Int = 0
    var canMove: Boolean = true

}
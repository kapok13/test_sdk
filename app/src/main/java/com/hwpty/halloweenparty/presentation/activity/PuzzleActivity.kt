package com.hwpty.halloweenparty.presentation.activity

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hwpty.halloweenparty.R
import com.hwpty.halloweenparty.presentation.factory.ContextViewModelFactory
import com.hwpty.halloweenparty.presentation.fragment.PuzzleSolvedDialog
import com.hwpty.halloweenparty.presentation.listener.PuzzleListener
import com.hwpty.halloweenparty.presentation.manager.Piece
import com.hwpty.halloweenparty.presentation.viewmodel.PuzzleViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt

class PuzzleActivity : AppCompatActivity(R.layout.activity_puzzle) {

    private lateinit var pieces: java.util.ArrayList<Piece>

    private val puzzleVM by viewModels<PuzzleViewModel> { ContextViewModelFactory(applicationContext) }

    private var currentResId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)
        getPictureCode()
        val layout = findViewById<RelativeLayout>(R.id.layout)
        val imageView =
            findViewById<ImageView>(R.id.imageView)
        imageView.post {
            currentResId = puzzleVM.puzzleImageResId
            setPic(imageView, currentResId)
            pieces = splitImage()
            val touchListener = PuzzleListener(this@PuzzleActivity)
            pieces.shuffle()
            for (piece in pieces) {
                piece.setOnTouchListener(touchListener)
                layout.addView(piece)
                val lParams =
                    piece.layoutParams as RelativeLayout.LayoutParams
                lParams.leftMargin =
                    Random().nextInt(layout.width - piece.pieceWidth)
                lParams.topMargin = layout.height - piece.pieceWidth
                piece.layoutParams = lParams
            }
        }
    }

    private fun getPictureCode() {
        puzzleVM.puzzleImageResId = when (intent?.getIntExtra("picturecode", 0)) {
            1 -> R.drawable.first_puzzle
            2 -> R.drawable.second_puzzle
            3 -> R.drawable.third_puzzle
            4 -> R.drawable.fourth_puzzle
            5 -> R.drawable.fifth_puzzle
            6 -> R.drawable.six_puzzle
            7 -> R.drawable.seventh_puzzle
            8 -> R.drawable.eight_puzzle
            9 -> R.drawable.nine_puzzle
            else -> puzzleVM.puzzleImageResId
        }
    }

    private fun setPic(imageView: ImageView, imagerResId: Int) {
        // Get the dimensions of the View
        val targetW = imageView.width
        val targetH = imageView.height

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = (photoW / targetW).coerceAtMost(photoH / targetH)


        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true
        val icon = BitmapFactory.decodeResource(
            this.resources,
            imagerResId, bmOptions
        )
        imageView.setImageBitmap(icon)
    }

    private fun splitImage(): java.util.ArrayList<Piece> {
        val piecesNumber = 12
        val rows = 4
        val cols = 3
        val imageView =
            findViewById<ImageView>(R.id.imageView)
        val pieces =
            ArrayList<Piece>(piecesNumber)

        // Get the scaled bitmap of the source image
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val dimensions = getBitmapPositionInsideImageView(imageView)
        val scaledBitmapLeft = dimensions[0]
        val scaledBitmapTop = dimensions[1]
        val scaledBitmapWidth = dimensions[2]
        val scaledBitmapHeight = dimensions[3]
        val croppedImageWidth = scaledBitmapWidth - 2 * abs(scaledBitmapLeft)
        val croppedImageHeight = scaledBitmapHeight - 2 * abs(scaledBitmapTop)
        val scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, scaledBitmapWidth, scaledBitmapHeight, true)
        val croppedBitmap = Bitmap.createBitmap(
            scaledBitmap,
            abs(scaledBitmapLeft),
            abs(scaledBitmapTop),
            croppedImageWidth,
            croppedImageHeight
        )

        // Calculate the with and height of the pieces
        val pieceWidth = croppedImageWidth / cols
        val pieceHeight = croppedImageHeight / rows

        // Create each bitmap piece and add it to the resulting array
        var yCoord = 0
        for (row in 0 until rows) {
            var xCoord = 0
            for (col in 0 until cols) {
                // calculate offset for each piece
                var offsetX = 0
                var offsetY = 0
                if (col > 0) {
                    offsetX = pieceWidth / 3
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3
                }

                // apply the offset to each piece
                val pieceBitmap = Bitmap.createBitmap(
                    croppedBitmap,
                    xCoord - offsetX,
                    yCoord - offsetY,
                    pieceWidth + offsetX,
                    pieceHeight + offsetY
                )
                val piece = Piece(applicationContext)
                piece.setImageBitmap(pieceBitmap)
                piece.xCord = xCoord - offsetX + imageView.left
                piece.yCord = yCoord - offsetY + imageView.top
                piece.pieceWidth = pieceWidth + offsetX
                piece.pieceHeight = pieceHeight + offsetY

                // this bitmap will hold our final puzzle piece image
                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth + offsetX,
                    pieceHeight + offsetY,
                    Bitmap.Config.ARGB_8888
                )

                // draw path
                val bumpSize = pieceHeight / 4
                val canvas = Canvas(puzzlePiece)
                val path = Path()
                path.moveTo(offsetX.toFloat(), offsetY.toFloat())
                if (row == 0) {
                    // top side piece
                    path.lineTo(pieceBitmap.width.toFloat(), offsetY.toFloat())
                } else {
                    // top bump
                    path.lineTo(
                        offsetX + (pieceBitmap.width - offsetX) / 3.toFloat(),
                        offsetY.toFloat()
                    )
                    path.cubicTo(
                        offsetX + (pieceBitmap.width - offsetX) / 6.toFloat(),
                        offsetY - bumpSize.toFloat(),
                        offsetX + (pieceBitmap.width - offsetX) / 6 * 5.toFloat(),
                        offsetY - bumpSize.toFloat(),
                        offsetX + (pieceBitmap.width - offsetX) / 3 * 2.toFloat(),
                        offsetY.toFloat()
                    )
                    path.lineTo(pieceBitmap.width.toFloat(), offsetY.toFloat())
                }
                if (col == cols - 1) {
                    // right side piece
                    path.lineTo(pieceBitmap.width.toFloat(), pieceBitmap.height.toFloat())
                } else {
                    // right bump
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 3.toFloat()
                    )
                    path.cubicTo(
                        pieceBitmap.width - bumpSize.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 6.toFloat(),
                        pieceBitmap.width - bumpSize.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 6 * 5.toFloat(),
                        pieceBitmap.width.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 3 * 2.toFloat()
                    )
                    path.lineTo(pieceBitmap.width.toFloat(), pieceBitmap.height.toFloat())
                }
                if (row == rows - 1) {
                    // bottom side piece
                    path.lineTo(offsetX.toFloat(), pieceBitmap.height.toFloat())
                } else {
                    // bottom bump
                    path.lineTo(
                        offsetX + (pieceBitmap.width - offsetX) / 3 * 2.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                    path.cubicTo(
                        offsetX + (pieceBitmap.width - offsetX) / 6 * 5.toFloat(),
                        pieceBitmap.height - bumpSize.toFloat(),
                        offsetX + (pieceBitmap.width - offsetX) / 6.toFloat(),
                        pieceBitmap.height - bumpSize.toFloat(),
                        offsetX + (pieceBitmap.width - offsetX) / 3.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                    path.lineTo(offsetX.toFloat(), pieceBitmap.height.toFloat())
                }
                if (col == 0) {
                    // left side piece
                    path.close()
                } else {
                    // left bump
                    path.lineTo(
                        offsetX.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 3 * 2.toFloat()
                    )
                    path.cubicTo(
                        offsetX - bumpSize.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 6 * 5.toFloat(),
                        offsetX - bumpSize.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 6.toFloat(),
                        offsetX.toFloat(),
                        offsetY + (pieceBitmap.height - offsetY) / 3.toFloat()
                    )
                    path.close()
                }

                // mask the piece
                val paint = Paint()
                paint.color = -0x1000000
                paint.style = Paint.Style.FILL
                canvas.drawPath(path, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)

                // draw a white border
                var border = Paint()
                border.color = -0x7f000001
                border.style = Paint.Style.STROKE
                border.strokeWidth = 8.0f
                canvas.drawPath(path, border)

                // draw a black border
                border = Paint()
                border.color = -0x80000000
                border.style = Paint.Style.STROKE
                border.strokeWidth = 3.0f
                canvas.drawPath(path, border)

                // set the resulting bitmap to the piece
                piece.setImageBitmap(puzzlePiece)
                pieces.add(piece)
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return pieces
    }

    private fun getBitmapPositionInsideImageView(imageView: ImageView?): IntArray {
        val ret = IntArray(4)
        if (imageView == null || imageView.drawable == null) return ret

        // Get image dimensions
        // Get image matrix values and place them in an array
        val f = FloatArray(9)
        imageView.imageMatrix.getValues(f)

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        val scaleX = f[Matrix.MSCALE_X]
        val scaleY = f[Matrix.MSCALE_Y]

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        val d = imageView.drawable
        val origW = d.intrinsicWidth
        val origH = d.intrinsicHeight

        // Calculate the actual dimensions
        val actW = (origW * scaleX).roundToInt()
        val actH = (origH * scaleY).roundToInt()
        ret[2] = actW
        ret[3] = actH

        // Get image position
        // We assume that the image is centered into ImageView
        val imgViewW = imageView.width
        val imgViewH = imageView.height
        val top = (imgViewH - actH) / 2
        val left = (imgViewW - actW) / 2
        ret[0] = left
        ret[1] = top
        return ret
    }

    fun checkGameOver() {
        if (isGameOver()) {
            PuzzleSolvedDialog().show(supportFragmentManager, "solved")
            puzzleVM.addSolvedPic()
        }
    }

    private fun isGameOver(): Boolean {
        for (piece in pieces) {
            if (piece.canMove) {
                return false
            }
        }
        return true
    }
}
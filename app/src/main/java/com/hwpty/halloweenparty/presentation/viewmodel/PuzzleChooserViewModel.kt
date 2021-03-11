package com.hwpty.halloweenparty.presentation.viewmodel

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableFloat
import androidx.lifecycle.ViewModel
import com.hwpty.halloweenparty.data.repository.SolvedPicRepoImpl

class PuzzleChooserViewModel(context: Context) : ViewModel() {

    val fifthImageClickState = ObservableBoolean(false)
    val secImageClickState = ObservableBoolean(false)
    val thirdImageClickState = ObservableBoolean(false)
    val fourthImageClickState = ObservableBoolean(false)
    val sixthImageClickState = ObservableBoolean(false)
    val seventhImageClickState = ObservableBoolean(false)
    val eightsImageClickState = ObservableBoolean(false)
    val ninethImageClickState = ObservableBoolean(false)

    val secondImageAlphaState = ObservableFloat(0.2f)
    val thirdImageAlphaState = ObservableFloat(0.2f)
    val fourthImageAlphaState = ObservableFloat(0.2f)
    val fifthImageAlphaState = ObservableFloat(0.2f)
    val sixthImageAlphaState = ObservableFloat(0.2f)
    val seventhImageAlphaState = ObservableFloat(0.2f)
    val eightsImageAlphaState = ObservableFloat(0.2f)
    val ninesImageAlphaState = ObservableFloat(0.2f)

    private val puzzleRepo = SolvedPicRepoImpl.getPicRepoInstance(context)

    fun checkSolvedPuzzles() {
        if (puzzleRepo!!.solvedPics >= 1) {
            secImageClickState.set(true)
            secondImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 2) {
            thirdImageClickState.set(true)
            thirdImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 3) {
            fourthImageClickState.set(true)
            fourthImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 4) {
            fifthImageClickState.set(true)
            fifthImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 5) {
            sixthImageClickState.set(true)
            sixthImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 6) {
            seventhImageClickState.set(true)
            seventhImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 7) {
            eightsImageClickState.set(true)
            eightsImageAlphaState.set(1f)
        }
        if (puzzleRepo.solvedPics >= 8) {
            ninethImageClickState.set(true)
            ninesImageAlphaState.set(1f)
        }
    }

}
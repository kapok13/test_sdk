package com.hwpty.halloweenparty.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.hwpty.halloweenparty.data.repository.SolvedPicRepoImpl

class PuzzleViewModel(context: Context) : ViewModel() {

    private val solvedPuzzlesRepo = SolvedPicRepoImpl.getPicRepoInstance(context)

    var puzzleImageResId = 0

    fun addSolvedPic() {
        solvedPuzzlesRepo?.solvedPics = 1
    }
}
package com.hwpty.halloweenparty.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hwpty.halloweenparty.R
import com.hwpty.halloweenparty.databinding.ActivityPuzzleChooserBinding
import com.hwpty.halloweenparty.presentation.factory.ContextViewModelFactory
import com.hwpty.halloweenparty.presentation.viewmodel.PuzzleChooserViewModel

class PuzzleChooserActivity : AppCompatActivity() {

    private val chooserVM by viewModels<PuzzleChooserViewModel> {
        ContextViewModelFactory(
            applicationContext
        )
    }

    private lateinit var binder: ActivityPuzzleChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setSupportActionBar(binder.puzzleChooserToolbar)
        chooserVM.checkSolvedPuzzles()
    }

    private fun initBinding() {
        binder = DataBindingUtil.setContentView(this, R.layout.activity_puzzle_chooser)
        binder.viewmodel = chooserVM
    }

    private fun startPuzzleGameWithCode(code: Int) {
        Intent(this, PuzzleActivity::class.java).apply {
            putExtra("picturecode", code)
            startActivity(this)
        }
    }

    fun onFirstImageClicked(view: View) = startPuzzleGameWithCode(1)
    fun onSecondImageClicked(view: View) = startPuzzleGameWithCode(2)
    fun onThirdImageClicked(view: View) = startPuzzleGameWithCode(3)
    fun onFourthImageClicked(view: View) = startPuzzleGameWithCode(4)
    fun onFifthImageClicked(view: View) = startPuzzleGameWithCode(5)
    fun onSixthImageClicked(view: View) = startPuzzleGameWithCode(6)
    fun onSeventhImageClicked(view: View) = startPuzzleGameWithCode(7)
    fun onEightsImageClicked(view: View) = startPuzzleGameWithCode(8)
    fun onNinesImageClicked(view: View) = startPuzzleGameWithCode(9)
}
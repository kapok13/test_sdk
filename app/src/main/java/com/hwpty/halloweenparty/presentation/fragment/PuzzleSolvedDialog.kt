package com.hwpty.halloweenparty.presentation.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.hwpty.halloweenparty.R
import com.hwpty.halloweenparty.databinding.DialogPuzzleSolvedBinding
import com.hwpty.halloweenparty.presentation.activity.MenuActivity
import com.hwpty.halloweenparty.presentation.activity.PuzzleChooserActivity

class PuzzleSolvedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return with(AlertDialog.Builder(activity)) {
            val binder = DataBindingUtil.inflate<DialogPuzzleSolvedBinding>(
                requireActivity().layoutInflater,
                R.layout.dialog_puzzle_solved,
                null,
                false
            ).apply {
                toMenuBtn.setOnClickListener {
                    startActivity(
                        Intent(
                            activity?.baseContext,
                            MenuActivity::class.java
                        )
                    )
                }
                toPuzzlesBtn.setOnClickListener {
                    startActivity(
                        Intent(
                            activity?.baseContext,
                            PuzzleChooserActivity::class.java
                        )
                    )
                }
            }
            setView(binder.root)
            create()
        }
    }

    override fun onStart() {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onStart()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        startActivity(Intent(activity?.baseContext, PuzzleChooserActivity::class.java))
    }
}
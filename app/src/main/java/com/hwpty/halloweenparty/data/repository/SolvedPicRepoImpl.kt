package com.hwpty.halloweenparty.data.repository

import android.content.Context

class SolvedPicRepoImpl(private val context: Context) : SolvedPicRepo {

    companion object {
        private var solvedPicInstance: SolvedPicRepo? = null

        fun getPicRepoInstance(context: Context): SolvedPicRepo? {
            if (solvedPicInstance == null) solvedPicInstance = SolvedPicRepoImpl(context)
            return solvedPicInstance
        }
    }

    override var solvedPics: Int
        get() = context.getSharedPreferences("puzzles", Context.MODE_PRIVATE).getInt("solved", 0)
        set(value) {
            context.getSharedPreferences("puzzles", Context.MODE_PRIVATE).edit().putInt(
                "solved",
                context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
                    .getInt("solved", 0) + value
            ).apply()
        }

    override fun resetSolvedPicTable() {
        context.getSharedPreferences("puzzles", Context.MODE_PRIVATE).edit().putInt(
            "solved", 0
        ).apply()
    }

}
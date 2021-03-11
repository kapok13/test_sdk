package com.hwpty.halloweenparty.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hwpty.halloweenparty.R

class MenuActivity : AppCompatActivity(R.layout.activity_menu) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.menu_toolbar))
    }

    fun onExitBtnClicked(view: View) = finishAndRemoveTask()


    fun onPlayBtnClciked(view: View) =
        startActivity(Intent(this, PuzzleChooserActivity::class.java))

}
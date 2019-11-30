package com.example.corsiblocktappingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_finish.*

import kotlinx.android.synthetic.main.activity_main.*

class FinishActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var finishText: TextView
    private lateinit var csvButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        finishText = findViewById(R.id.finish_text)
        playButton = findViewById(R.id.play_button)
        csvButton = findViewById(R.id.csv_button)
        finishText.gravity = Gravity.CENTER
        finishText.text = "You reached Round: " + intent.getIntExtra("rounds", 0).toString()
        playButton.text = "Try Again"
        csvButton.text = "Export to csv"
        playButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

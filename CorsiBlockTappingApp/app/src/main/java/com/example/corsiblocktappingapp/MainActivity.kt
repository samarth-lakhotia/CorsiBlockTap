package com.example.corsiblocktappingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var settingsButton:Button
    private lateinit var prefs:SharedPreferences

    private val REQUEST_CODE_1 = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        playButton = findViewById(R.id.play_button)
        settingsButton = findViewById(R.id.settings_button)

        prefs =getSharedPreferences("configuration", Context.MODE_PRIVATE)
//        prefs.edit().clear().apply()

        playButton.setOnClickListener {
            if(prefs.getInt("difficulty",-1) != -1) {
                startActivityForResult(Intent(this, CountDownTimerActivity::class.java), REQUEST_CODE_1)

            } else{
                val intent = Intent()
                intent.putExtra("START_GAME",true)
                intent.setClass(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        settingsButton.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java))  }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_1) {
                startActivity(Intent(baseContext, GameActivity::class.java))
            }
        }
    }
}

package com.example.corsiblocktappingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast

class SettingsActivity : Activity() {
    private lateinit var difficultyRadioGroup: RadioGroup
    private lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences("configuration", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_user_preferences)
        difficultyRadioGroup = findViewById(R.id.difficulty_level)
        difficultyRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val editor = pref.edit()
            when (i) {
                R.id.easy_difficult -> {
                    editor.putInt("difficulty", DIFFICULTY.EASY_DIFFICULTY.id)
                }
                R.id.medium_difficult -> {
                    editor.putInt("difficulty", DIFFICULTY.MEDIUM_DIFFICULTY.id)
                }
                else -> {
                    editor.putInt("difficulty", DIFFICULTY.HARD_DIFFICULTY.id)
                }
            }
            editor.apply()
            var toastText ="Preferences have been saved successfully..."

            if (intent.getBooleanExtra("START_GAME", false)) {
                Toast.makeText(this, toastText+"Starting Game now...", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, GameActivity::class.java))
            }
            else{
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            }
        }
    }
}
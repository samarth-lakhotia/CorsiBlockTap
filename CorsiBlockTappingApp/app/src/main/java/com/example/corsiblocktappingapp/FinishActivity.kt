package com.example.corsiblocktappingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import models.BlockTap

import java.io.FileWriter
import java.io.IOException

class FinishActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var finishText: TextView
    private lateinit var csvButton: TextView
    private val csvHeader = "Datetime,TapPosition,ElapsedRoundTime(s),BlocksToRmr,Correct"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val roundData = intent.getSerializableExtra("roundData") as ArrayList<BlockTap>
        // writes to /storage/emulated/0/Android/data/com.example.corsiblocktappingapp/files/MyFileStorage because
        // getExternalStorageDirectory is now deprevated. This folder can be accessed via ES FileExplorer.
        val fileWriter = FileWriter(getExternalFilesDir("MyFileStorage").toString() + "/DataFromSession.csv")

        finishText = findViewById(R.id.finish_text)
        playButton = findViewById(R.id.play_button)
        csvButton = findViewById(R.id.csv_button)
        finishText.gravity = Gravity.CENTER
        finishText.text = """You reached Round: ${intent.getIntExtra("rounds", 0)}"""
        playButton.text = "Try Again"
        csvButton.text = "Export to csv"

        playButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        // writing the data from each round to a csv file
        csvButton.setOnClickListener {
            try {
                fileWriter.append(csvHeader)
                fileWriter.append('\n')

                for (data in roundData) {
                    fileWriter.append(data.tapTimestamp.toString())
                    fileWriter.append(',')
                    fileWriter.append(data.tapPositionWithRespectToGrid.toString())
                    fileWriter.append(',')
                    fileWriter.append(data.timeTappedSinceBeginning.toString())
                    fileWriter.append(',')
//                    fileWriter.append(data.currentBlocksToRmr.toString())
//                    fileWriter.append(',')
                    fileWriter.append(data.wasItCorrectlyTapped.toString())
                    fileWriter.append('\n')
                }

                Log.i("ASDF", "Wrote to CSV successfully!")
            } catch (e: Exception) {
                Log.i("ASDF", "Did not write to CSV successfully!")
                Log.i("ASDF", e.printStackTrace().toString())
            } finally {
                try {
                    fileWriter.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    Log.i("ASDF", "flushing/closing Error!")
                    Log.i("ASDF", e.printStackTrace().toString())
                }
            }
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

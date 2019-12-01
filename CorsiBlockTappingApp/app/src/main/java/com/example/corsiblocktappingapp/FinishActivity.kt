package com.example.corsiblocktappingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import models.BlockTap

import java.io.FileWriter
import java.io.IOException
import android.net.NetworkCapabilities
import android.net.Network
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.content.Context.CONNECTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class FinishActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var finishText: TextView
    private lateinit var csvButton: TextView
    private lateinit var firebaseButton: TextView
    private val csvHeader = "TapTimestamp,TapPositionWithRespectToGrid,TimeTappedSinceBeginning(ms),WasItCorrectlyTapped"
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val tapData = intent.getSerializableExtra("roundData") as ArrayList<BlockTap>
        // writes to /storage/emulated/0/Android/data/com.example.corsiblocktappingapp/files/MyFileStorage because
        // getExternalStorageDirectory is now deprecated. This folder can be accessed via ES FileExplorer.
        val fileWriter = FileWriter(getExternalFilesDir("MyFileStorage").toString() + "/DataFromSession.csv")

        finishText = findViewById(R.id.finish_text)
        playButton = findViewById(R.id.play_button)
        csvButton = findViewById(R.id.csv_button)
        firebaseButton = findViewById(R.id.firebase_button)

        finishText.gravity = Gravity.CENTER
        finishText.text = """You reached Round: ${intent.getIntExtra("rounds", 0)}"""
        playButton.text = "Try Again"
        firebaseButton.text = "Upload to Firebase"

        playButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        csvButton.text = "Export to csv"
        // writing the data from each round to a csv file
        csvButton.setOnClickListener {
            try {
                fileWriter.append(csvHeader)
                fileWriter.append('\n')

                for (data in tapData) {
                    fileWriter.append(data.tapTimestamp.toString())
                    fileWriter.append(',')
                    fileWriter.append(data.tapPositionWithRespectToGrid.toString())
                    fileWriter.append(',')
                    fileWriter.append(data.timeTappedSinceBeginning.toString())
                    fileWriter.append(',')
                    fileWriter.append(data.wasItCorrectlyTapped.toString())
                    fileWriter.append('\n')
                }

                Log.i("ASDF", "Wrote to CSV successfully!")
                Toast.makeText(this, "Wrote to CSV successfully", Toast.LENGTH_LONG)
                    .show()
            } catch (e: Exception) {
                Log.i("ASDF", "Did not write to CSV successfully!")
                Toast.makeText(this, "Did not write to CSV successfully", Toast.LENGTH_LONG)
                    .show()
                Log.i("ASDF", e.printStackTrace().toString())
            } finally {
                try {
                    fileWriter.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    Log.i("ASDF", "flushing/closing Error!")
                    Toast.makeText(this, "Closing Error", Toast.LENGTH_LONG)
                        .show()
                    Log.i("ASDF", e.printStackTrace().toString())
                }
            }
        }

        dbReference = FirebaseDatabase.getInstance().getReference("usersData")
        firebaseButton.setOnClickListener {
            Toast.makeText(this, "Uploading data to Firebase", Toast.LENGTH_LONG)
                .show()

            tapData.forEach {
                val key = dbReference.child("usersData").push().key
                it.uuid = key!!
                dbReference.child("taps").child(key).setValue(it)
            }
        }


        // if WiFi is enabled, then make the firebase upload button enabled
        firebaseButton.isEnabled = (checkForNetwork(baseContext))
        // otherwise, show a toast telling the user to connect to WiFi
        if (!checkForNetwork(baseContext)) {
            Toast.makeText(this, "Please connect to WiFi in order to upload data to Firebase", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun checkForNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // if past API level 23, use the new method. Otherwise, use the deprecated method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
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

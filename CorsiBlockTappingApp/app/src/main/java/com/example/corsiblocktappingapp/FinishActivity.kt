package com.example.corsiblocktappingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.io.FileWriter
import java.io.IOException
import android.net.NetworkCapabilities
import android.net.ConnectivityManager
import android.os.Build

// LOGIN INFORMATION FOR TEMPORARY FIREBASE ACCOUNT THAT WAS CREATED:
// user: CorsiTapping@gmail.com
// pass: corsi2019

class FinishActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var finishText: TextView
    private lateinit var jsonButton: TextView
    private lateinit var firebaseButton: TextView
    private val csvHeader = "Round, Difficulty, Round Details, Additional Info"
    private lateinit var dbReference: DatabaseReference

    //Vars for the total time
    private lateinit var totalTime: String
    private lateinit var totalTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        // receives the gameSession from the GameActivity
        val gameData = intent.getStringExtra("GameData")
        val jsonString = intent.getStringExtra("GameSessionJSON")


        finishText = findViewById(R.id.finish_text)
        playButton = findViewById(R.id.play_button)
        jsonButton = findViewById(R.id.csv_button)
        firebaseButton = findViewById(R.id.firebase_button)

        //Getting Total time
        totalTime = intent.getStringExtra("TotalTime")
        totalTimeTextView = findViewById(R.id.finish_time)
        totalTimeTextView.gravity = Gravity.CENTER
        totalTimeTextView.text = totalTime

        finishText.gravity = Gravity.CENTER
        finishText.text = """You reached Round: ${intent.getIntExtra("rounds", 0)}"""
        playButton.text = "Try Again"
        firebaseButton.text = "Upload to Firebase"

        // the button that triggers another game
        playButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        jsonButton.text = getString(R.string.export_to_json)
        // writing the data from each round to a csv file
        jsonButton.setOnClickListener {
            writeJSONFile(jsonString)
        }

        // getting the reference to the FirebaseDatabase
        dbReference = FirebaseDatabase.getInstance().getReference("usersData")

        // when the user clicks on the upload button, upload the data to firebase
        firebaseButton.setOnClickListener {
            Toast.makeText(this, "Uploading data to Firebase", Toast.LENGTH_LONG)
                .show()

            Log.i("CSV", gameData.toString())
            dbReference.setValue(gameData)
        }


        // if WiFi is enabled, then make the firebase upload button enabled
        firebaseButton.isEnabled = (checkForNetwork(baseContext))

        // otherwise, show a toast telling the user to connect to WiFi
        if (!checkForNetwork(baseContext)) {
            Toast.makeText(
                this,
                "Please connect to WiFi in order to upload data to Firebase",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun writeJSONFile(jsonString: String?) {

        // writes to /storage/emulated/0/Android/data/com.example.corsiblocktappingapp/files/MyFileStorage because
        // getExternalStorageDirectory is now deprecated. This folder can be accessed via ES FileExplorer.
        val jsonFileWriter =
            FileWriter(getExternalFilesDir("MyFileStorage").toString() + "/DataFromSession.json")

        try {

            jsonFileWriter.write(jsonString!!)
            jsonFileWriter.flush()
            jsonFileWriter.close()

            Log.i("JSON", "Wrote to CSV successfully!")
            Toast.makeText(this, "Wrote to JSON successfully", Toast.LENGTH_LONG)
                .show()
        } catch (e: Exception) {
            // do this when any Exception is caught
            Toast.makeText(this, "Did not write to CSV successfully", Toast.LENGTH_LONG)
                .show()
            Log.i("JSON", e.printStackTrace().toString())
        } finally {
            try {
                jsonFileWriter.flush()
                jsonFileWriter.close()
            } catch (e: IOException) {
                // if unable to flush or close successfully, log the info
                Log.i("JSON", "flushing/closing Error!")
                Toast.makeText(this, "Closing Error", Toast.LENGTH_LONG)
                    .show()
                Log.i("JSON", e.printStackTrace().toString())
            }
        }
    }

    // a function that checks for wifi connection
    private fun checkForNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
    
}

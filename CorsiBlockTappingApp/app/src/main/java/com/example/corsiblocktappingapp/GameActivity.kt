package com.example.corsiblocktappingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.HashSet
import kotlin.random.Random

class GameActivity : Activity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var drawnPattern = HashSet<Int>()
    private lateinit var iter: Iterator<Int>
    private val NUMBER_OF_BLOCKS = 20
    private val NUMBER_TO_REMEMBER = 5
    private var currentNumberToRemember = NUMBER_TO_REMEMBER
    private val COLS_IN_GRID = 5
    private lateinit var nextButton: Button
    private lateinit var resetButton: Button
    private lateinit var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private var numRounds = 0
    private lateinit var rounds: ArrayList<TappingRound>

    //Var for Timer
    private lateinit var mTimerTextView: Chronometer
    private lateinit var mTimer: Chronometer
    private var mTimerRunning: Boolean = false
    private var mTimerTerm: Long = 0
    private var mTimerTotal: Long = 0


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_page)

        nextButton = findViewById(R.id.next_button)
        nextButton.setOnClickListener { startRound(generateRandom()) }

        resetButton = findViewById(R.id.restart_button)
        resetButton.setOnClickListener {
            resetGame()
            resetAllBocks()
            startRound(generateRandom())
        }
        builder = AlertDialog.Builder(this)
        builder.setTitle("Do you want to play again?")
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    startRound(generateRandom())
                }
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(
                    this,
                    "Negative/No button clicked.",
                    Toast.LENGTH_LONG
                ).show()
                DialogInterface.BUTTON_NEUTRAL -> Toast.makeText(
                    this,
                    "Neutral/Cancel button clicked.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //Timer Set Up
        mTimerTextView = findViewById(R.id.total_time)
        mTimerTextView.format = "Total Time: %s"
        mTimerTextView.base = SystemClock.elapsedRealtime()
        mTimer = findViewById(R.id.game_timer)
        mTimer.format = "Time: %s"
        mTimer.base = SystemClock.elapsedRealtime()


        rounds = ArrayList()
        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("CANCEL", dialogClickListener)

        // Initialize the AlertDialog using builder object
        alertDialog = builder.create()

        recyclerView = findViewById<RecyclerView>(R.id.corsi_grid)
        viewManager = GridLayoutManager(this, COLS_IN_GRID)
        var arr = Array(NUMBER_OF_BLOCKS) { "" }
        viewAdapter = GameAdapter(arr)

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager


    }

    override fun onResume() {
        super.onResume()
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                startRound(generateRandom())
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this) //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun startRound(patternToMatch: HashSet<Int>) {
        val handle = Handler()

        nextButton.isEnabled = false
        resetButton.isEnabled = false
        setPatternSetterListener() // Listener to turn red upon clicking
        unlockAllBlocks(false)

        iter = patternToMatch.iterator()
        var currRound = TappingRound(numRounds++, NUMBER_OF_BLOCKS, patternToMatch)

        rounds.add(currRound)
        var i = patternToMatch.iterator()
        val runnable = object : Runnable {
            override fun run() {
                if (i.hasNext()) {
                    var block = recyclerView[i.next()]
                    block.performClick()
                    handle.postDelayed(this, 1000)
                } else {
                    Log.i("PATTERN UPDATE", "PATTERN DRAWN. RECORDING PATTERN NOW...")
                    recordUserInput()
                    handle.removeCallbacks(this)
                }
            }
        }
        handle.post(runnable)
    }

    fun recordUserInput() {
//        startButton.visibility = View.GONE
//        recordButton.visibility = View.GONE
//        recordButton.isEnabled = false
//        doneButton.visibility = View.VISIBLE

        unlockAllBlocks(true)
        resetAllBocks() // Reset All Blocks to default background for user to enter their pattern
        setRecordPatternListener()
        startTimer()
    }


    fun doneRecording() {
//        doneButton.visibility = View.GONE
//        startButton.visibility = View.VISIBLE
//        recordButton.visibility = View.VISIBLE
        setPatternSetterListener()
        resetAllBocks()
        unlockAllBlocks(false)
        Log.i("Drawn Pattern", rounds.last().getTimestamps().toString())

//        alertDialog.show()
        if (rounds.last().correctlyEntered) {
            nextButton.isEnabled = true
            currentNumberToRemember++
        } else {
            resetButton.isEnabled = true
            nextButton.isEnabled = false
        }

        stopTimer(rounds.last().correctlyEntered)
        rounds.last().endRound(mTimerTerm)
        Log.i("TIME", mTimerTerm.toString())
    }

    fun resetGame() {
        currentNumberToRemember = NUMBER_TO_REMEMBER
        rounds.clear()

        //Timer Clean
        timerClean()
    }

    fun unlockAllBlocks(bool: Boolean = false) {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].isEnabled = bool
        }
    }

    @SuppressLint("NewApi")
    fun resetAllBocks() {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].backgroundTintList = getColorStateList(R.color.block_color_default)
            recyclerView[i].isClickable = true
            recyclerView[i].isEnabled = true
        }
    }

    fun recordTap(set: HashSet<Int>, position: Int): Boolean {
        if (!iter.hasNext()) {
            Toast.makeText(this, "Incorrect Sequence - Exceeded. Going Back", Toast.LENGTH_LONG)
                .show()
            return false
        } else {
            rounds.last().stampIt()
            if (iter.next() != position) {
                Toast.makeText(this, "Incorrect Sequence. Going Back..", Toast.LENGTH_LONG).show()
                return false
            }

            set.add(position)
            return true
        }
    }

    fun generateRandom(): HashSet<Int> {
        val k = currentNumberToRemember
        var retSet = HashSet<Int>()
        while (retSet.size != k) {
            retSet.add(Random.nextInt(NUMBER_OF_BLOCKS))
        }
        return retSet
    }

    @SuppressLint("NewApi")
    fun setRecordPatternListener() {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].setOnClickListener(null)
            recyclerView[i].setOnClickListener {

                if (it.isClickable) {
                    it.backgroundTintList = getColorStateList(R.color.block_color_recording_pattern)
                    if (!recordTap(drawnPattern, i)) {
                        doneRecording()
                    } else {
                        if (!iter.hasNext()) {
                            Toast.makeText(this, "You got this correct!", Toast.LENGTH_LONG).show()
                            rounds.last().correctlyEntered = true
                            doneRecording()

                        }
                        it.isClickable = !it.isClickable
                    }
                } else {
                    it.backgroundTintList = getColorStateList(R.color.block_color_default)
                    it.isClickable = !it.isClickable
                }

            }

        }
    }

    @SuppressLint("NewApi")
    fun setPatternSetterListener() {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].setOnClickListener(null)
            recyclerView[i].setOnClickListener {
                if (it.isClickable) {
//                    it.setBackgroundColor(Color.RED)
                    it.backgroundTintList = getColorStateList(R.color.block_color_setting_pattern)
                } else {
                    it.backgroundTintList = getColorStateList(R.color.block_color_default)
                }
                it.isClickable = !it.isClickable
            }
        }
    }

    fun getStoredBackground(view: Button): ColorStateList? {
        return (recyclerView.findContainingViewHolder(view) as GameAdapter.GameViewHolder).bg
    }

    //Timer Functions
    fun startTimer() {
        if (!mTimerRunning) {
            mTimerTerm = 0
            mTimer.base = SystemClock.elapsedRealtime() - mTimerTerm
            mTimer.start()
            mTimerRunning = true
        }
    }

    //Timer Functions
    fun stopTimer(correct: Boolean): Long {
        if (mTimerRunning) {
            mTimer.stop()
            when (correct) {
                true -> mTimerTerm = SystemClock.elapsedRealtime() - mTimer.base
                false -> mTimerTerm = 0
            }
            mTimerRunning = false
            mTimer.base = SystemClock.elapsedRealtime()
        } else mTimerTerm = 0

        //Timer Text View
        mTimerTotal += mTimerTerm
        timerTextViewF()

        return mTimerTerm
    }

    //Timer TextView Function
    fun timerTextViewF() {
        mTimerTextView.base = SystemClock.elapsedRealtime() - mTimerTotal
    }

    //Timer Clean
    fun timerClean() {
        mTimerTerm = 0
        mTimerTotal = 0
        mTimer.base = SystemClock.elapsedRealtime()
        mTimerTextView.base = SystemClock.elapsedRealtime()
        mTimerRunning = false

    }
}
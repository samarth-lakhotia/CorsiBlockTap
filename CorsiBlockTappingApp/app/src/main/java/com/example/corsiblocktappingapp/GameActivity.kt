package com.example.corsiblocktappingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import models.BlockTap
import models.GameSession
import kotlin.collections.HashSet
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GameActivity : Activity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var nextButton: Button
    private lateinit var resetButton: Button
    private lateinit var numTriesTextView: TextView
    private lateinit var mTimerTextView: Chronometer
    private lateinit var mTimer: Chronometer

    private lateinit var userPreferences: SharedPreferences

    private lateinit var gameSession: GameSession
    private lateinit var gameSessions:ArrayList<GameSession>
    private lateinit var difficulty: DIFFICULTY
    val blocksData = ArrayList<BlockTap>()
    private lateinit var iter: Iterator<Int>
    private var mTimerRunning: Boolean = false
    private var mTimerTerm: Long = 0
    private var mTimerTotal: Long = 0

    //Var for Timer

    private var NUMBER_OF_BLOCKS = -1
    private var NUMBER_TO_REMEMBER = -1
    private var currentNumberToRemember = NUMBER_TO_REMEMBER
    private val COLS_IN_GRID = Constants.COLS_IN_GRID


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        userPreferences = getSharedPreferences("configuration", Context.MODE_PRIVATE)
        difficulty=setDifficultyConfigurations()


        gameSession = GameSession(difficulty)
        gameSessions = ArrayList()
        gameSessions.add(gameSession)

        nextButton = findViewById(R.id.next_button)
        nextButton.setOnClickListener { startRound(generateRandom()) }

        numTriesTextView = findViewById(R.id.num_tries_view)

        resetButton = findViewById(R.id.restart_button)
        resetButton.setOnClickListener {
            resetGame()
            resetAllBocks()
            startRound(generateRandom())
        }

        //Timer Set Up
        mTimerTextView = findViewById(R.id.total_time)
        mTimerTextView.format = "Total Time: %s"
        mTimerTextView.base = SystemClock.elapsedRealtime()
        mTimer = findViewById(R.id.game_timer)
        mTimer.format = "Time: %s"
        mTimer.base = SystemClock.elapsedRealtime()

        // Setting up the recycler view to populate the grid with the given number of columns

        recyclerView = findViewById(R.id.corsi_grid)
        viewManager = GridLayoutManager(this, COLS_IN_GRID)
        var arr = Array(NUMBER_OF_BLOCKS) { "" }
        viewAdapter = GameAdapter(arr)

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager


    }

    private fun setDifficultyConfigurations(): DIFFICULTY {
        var difficulty = userPreferences.getInt("difficulty", -1)
        var diff: DIFFICULTY
        when (difficulty) {
            DIFFICULTY.EASY_DIFFICULTY.id -> {
                diff = DIFFICULTY.EASY_DIFFICULTY

            }
            DIFFICULTY.MEDIUM_DIFFICULTY.id -> {
                diff = DIFFICULTY.MEDIUM_DIFFICULTY

            }
            else -> {
                diff = DIFFICULTY.HARD_DIFFICULTY
            }
        }
        NUMBER_OF_BLOCKS = diff.NUMBER_OF_BLOCKS
        NUMBER_TO_REMEMBER = diff.INITIAL_BLOCKS_TO_REMEMBER
        currentNumberToRemember = NUMBER_TO_REMEMBER
        return diff

    }

    override fun onResume() {
        super.onResume()

        // This observer listener ensures that the pattern starts to record only after all the blocks
        // are loaded in the foreground.
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                startRound(generateRandom())
                // Removing this listener such that it does not get stuck in an infinite loop
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this) //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun startRound(patternToMatch: HashSet<Int>, restart: Boolean = false) {
        // Disable the start and reset button while the computer is tapping the blocks
        nextButton.isEnabled = false
        resetButton.isEnabled = false

        setPatternSetterListener() // Listener to turn red upon clicking

        // Lock all the blocks so that the user does not click while the computer is
        unlockAllBlocks(false)



        // If this method has been called as a restart of the current round, this means that the user
        // has entered the wrong pattern for the current round and can try again
        if (!restart) {
            gameSession.addRound(patternToMatch)
        }

        // Iterator for the current pattern
        iter = patternToMatch.iterator()

        // Update the number of tries left for the current round
        gameSession.tryRound()

        // Update the text view with the new value
        setNumberOfTries(gameSession.getNumberOfTriesLeftForCurrentRound())

        // Call this method that taps the blocks mentioned in the pattern
        tapBlocks(patternToMatch)
    }

    private fun tapBlocks(patternToMatch: HashSet<Int>) {
        var handle = Handler()
        var iteratorForTappingBlocks = patternToMatch.iterator()

        // runnable object that taps the blocks every 1 second
        val runnable = object : Runnable {
            override fun run() {
                if (iteratorForTappingBlocks.hasNext()) {
                    var block = recyclerView[iteratorForTappingBlocks.next()]
                    block.performClick()
                    handle.postDelayed(this, 1000)
                } else {
                    // If all the blocks have been tapped, then it is turn of the user to enter in theirs
                    recordUserInput()
                    handle.removeCallbacks(this)
                }
            }
        }
        handle.post(runnable)
    }

    fun recordUserInput() {
        // User's turn to enter their pattern.
        // We provide the user to restart the game at this point. Can be commented out if not needed
        resetButton.isEnabled = true

        // Unlock all the blocks for the user to tap blocks
        unlockAllBlocks(true)

        resetAllBocks() // Reset All Blocks to default background for user to enter their pattern

        // Set the listener for blocks to turn to the color for recording user pattern
        setRecordPatternListener()

        // Start the timer for the current round as well the continuation of the timer for the whole
        // game
        startTimer()
    }


    fun doneRecording() {
        // Once user has entered their pattern, this method is called to wind up the round and proceed
//        next round or restart the game

        // Go back to the default listener
        setPatternSetterListener()
        resetAllBocks()

        // Lock all the blocks so that user does not play around while the game is not in any round
        unlockAllBlocks(false)

        // If the user has correctly entered, then allow them to go the next round by enabling the
        // next button and increasing the number of blocks to remember in the next round by 1
        if (gameSession.checkIfUserWonCurrentRound()) {
            nextButton.isEnabled = true
            currentNumberToRemember++
            gameSession.endSession(mTimerTotal)
        } else {
//            If the user has inputted the wrong pattern, check if they have any more tries left
//            If they do not have any tries left, then the game is over and disable the next button
            if (!gameSession.getTheLatestRound().areThereTriesLeft()) {
                resetButton.isEnabled = true
                nextButton.isEnabled = false
                gameSession.endSession(mTimerTotal)

                var completeResults = Intent(this, FinishActivity::class.java)
                completeResults.putExtra("rounds", gameSession.getNumberOfRoundsPlayed())
                completeResults.putExtra("roundData", gameSession.getTheLatestRound().getUserTaps())
                startActivity(completeResults)
            } else {
//                else the start the same round again
                startRound(gameSession.getTheLatestPatternToRemember(), true)
            }

        }
//Stop timer for the last round and update the time for the current round
        stopTimer(gameSession.checkIfUserWonCurrentRound())
        gameSession.endTheCurrentRound(mTimerTerm)

    }

    fun resetGame() {
//        Reset to the default values
        gameSession = GameSession(difficulty)
        gameSessions.add(gameSession)
        currentNumberToRemember=gameSession.getInitialNumberOfBlocksToRemember()
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

    //    Record a single user tap. Method associated the tap with information about the tap
    @SuppressLint("NewApi")
    fun recordTap(position: Int): Boolean {
        val milliRoundTime = SystemClock.elapsedRealtime() - mTimer.base
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
        dateFormatter.setLenient(false)
        val s = dateFormatter.format(Date())

        val wasItCorrectlyTapped = gameSession.addTap(position, milliRoundTime)
        return if (!wasItCorrectlyTapped) {
            Toast.makeText(this, "Incorrect Sequence", Toast.LENGTH_LONG)
                .show()
            false
        } else {
            if (gameSession.checkIfUserWonCurrentRound())
                Toast.makeText(this, "You got this correct", Toast.LENGTH_LONG)
                    .show()
            true
        }
    }

    //Generate a random sequence with the current number to remember
    fun generateRandom(): HashSet<Int> {
        val k = currentNumberToRemember
        var retSet = HashSet<Int>()
        while (retSet.size != k) {
            retSet.add(Random.nextInt(NUMBER_OF_BLOCKS))
        }
        return retSet
    }

    /*Listeners for the blocks*/
    @SuppressLint("NewApi")
    fun setRecordPatternListener() {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].setOnClickListener(null)
            recyclerView[i].setOnClickListener {

                if (it.isClickable) {
                    it.backgroundTintList = getColorStateList(R.color.block_color_recording_pattern)

                    if (!recordTap(i)) {
                        doneRecording()
                    } else {
                        if (gameSession.checkIfUserWonCurrentRound()) {
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

    fun setNumberOfTries(triesLeft: Int) {
        numTriesTextView.text = "${resources.getString(R.string.tries_left)} ${triesLeft}"
    }
}
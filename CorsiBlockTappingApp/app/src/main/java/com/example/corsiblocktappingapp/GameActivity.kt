package com.example.corsiblocktappingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
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
    //    private lateinit var startButton: Button
//    private lateinit var recordButton: Button
//    private lateinit var doneButton: Button
    private lateinit var mBg: Drawable
    private var drawnPattern = HashSet<Int>()
    private lateinit var iter: Iterator<Int>
    private val NUMBER_OF_BLOCKS = 20
    private var NUMBER_TO_REMEMBER = 5
    private val COLS_IN_GRID = 5
    private lateinit var builder:AlertDialog.Builder
    private lateinit var alertDialog:AlertDialog
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_page)
        builder = AlertDialog.Builder(this)
        builder.setTitle("Do you want to play again?")
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {NUMBER_TO_REMEMBER++;drawGeneratedPattern(generateRandom())}
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(this,"Negative/No button clicked.",Toast.LENGTH_LONG).show()
                DialogInterface.BUTTON_NEUTRAL -> Toast.makeText(this,"Neutral/Cancel button clicked.",Toast.LENGTH_LONG).show()
            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO",dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("CANCEL",dialogClickListener)


        // Initialize the AlertDialog using builder object
        alertDialog = builder.create()

//        startButton = findViewById(R.id.start_button)
//        recordButton = findViewById(R.id.record_button)
//        doneButton = findViewById(R.id.done_button)
//        startButton.setOnClickListener {
//            drawGeneratedPattern(generateRandom())
//        }
//        recordButton.setOnClickListener {
//            recordUserPattern()
//        }
//        doneButton.setOnClickListener {
//            doneRecording()
//        }

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
                drawGeneratedPattern(generateRandom())
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this) //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    private fun drawGeneratedPattern(patternToMatch: HashSet<Int>) {
        val handle = Handler()
        setDefaultListeners()
        unlockAllBlocks(false)
        iter = patternToMatch.iterator()
        var i = patternToMatch.iterator()
        val runnable = object : Runnable {
            override fun run() {
                if (i.hasNext()) {
                    var block = recyclerView[i.next()]
                    block.performClick()
                    handle.postDelayed(this, 1000)
                } else {
//                    recordButton.isEnabled = true
                    Log.i("PATTERN ENDED", "PATTERN ENDED")
                    recordUserPattern()
                    handle.removeCallbacks(this)
                }
            }
        }
        handle.post(runnable)
    }

    @SuppressLint("NewApi")
    fun recordUserPattern() {
//        startButton.visibility = View.GONE
//        recordButton.visibility = View.GONE
//        recordButton.isEnabled = false
//        doneButton.visibility = View.VISIBLE

        unlockAllBlocks(true)
        resetAllBocks()
        setRecordListener()

    }


    fun doneRecording() {
//        doneButton.visibility = View.GONE
//        startButton.visibility = View.VISIBLE
//        recordButton.visibility = View.VISIBLE
        setDefaultListeners()
        resetAllBocks()
        Log.i("Drawn Pattern", drawnPattern.toString())
        alertDialog.show()
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
            if (iter.next() != position) {
                Toast.makeText(this, "Incorrect Sequence. Going Back..", Toast.LENGTH_LONG).show()
                return false
            }

            set.add(position)
            return true
        }
    }

    fun generateRandom(): HashSet<Int> {
        val k = NUMBER_TO_REMEMBER
        var retSet = HashSet<Int>()
        while (retSet.size != k) {
            retSet.add(Random.nextInt(NUMBER_OF_BLOCKS))
        }
        return retSet
    }

    @SuppressLint("NewApi")
    fun setRecordListener() {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].setOnClickListener(null)
            recyclerView[i].setOnClickListener {

                if (it.isClickable) {
                    it.backgroundTintList = getColorStateList(R.color.block_color_green)
                    if (!recordTap(drawnPattern, i)) {
                        doneRecording()
                    } else {
                        if (!iter.hasNext()) {
                            Toast.makeText(this, "You got this correct!", Toast.LENGTH_LONG).show()
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
    fun setDefaultListeners() {
        for (i in 0 until viewAdapter.itemCount) {
            recyclerView[i].setOnClickListener(null)
            recyclerView[i].setOnClickListener {
                if (it.isClickable) {
//                    it.setBackgroundColor(Color.RED)
                    it.backgroundTintList = getColorStateList(R.color.block_color_state)
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
}
package com.example.corsiblocktappingapp

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.core.os.postDelayed
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet
import kotlin.random.Random
import kotlin.concurrent.schedule
class GameActivity:Activity(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var startButton: Button
    private var mMoverFuture: ScheduledFuture<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_page)
        startButton = findViewById(R.id.start_button)
        startButton.setOnClickListener {
            drawPattern()
        }
        viewManager = GridLayoutManager(this,5)
        var arr = Array(25){""}


        viewAdapter = GameAdapter(arr)

        recyclerView = findViewById<RecyclerView>(R.id.corsi_grid).apply {
            setHasFixedSize(true)
            layoutManager=viewManager
            adapter = viewAdapter
        }
    }

    private fun drawPattern() {
        val pattern = generateRandom()
        val handle = Handler()

        var i=pattern.iterator()
        val runnable = object:Runnable {
            override fun run() {
                if (i.hasNext()) {
                    recyclerView[i.next()].performClick()
                    handle.postDelayed(this, 1000)
                }
                else{
                    Log.i("PATTERN ENDED", "PATTERN ENDED")
                    handle.removeCallbacks(this)
                }
            }
        }

        handle.post(runnable)

    }

    fun generateRandom(): HashSet<Int> {
        val k = 5
        var retSet = HashSet<Int>()
        while(retSet.size!=k){
            retSet.add(Random.nextInt(25))
        }
        return retSet
    }
}
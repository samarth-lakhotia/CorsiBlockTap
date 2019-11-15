package com.example.corsiblocktappingapp

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameActivity:Activity(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_page)

        viewManager = GridLayoutManager(this,5)
        viewAdapter = GameAdapter(arrayOf("1","2","3","4","5","6","7","8","9","10", "11","12","13","14","15"))

        recyclerView = findViewById<RecyclerView>(R.id.corsi_grid).apply {
            setHasFixedSize(true)
            layoutManager=viewManager
            adapter = viewAdapter
        }


    }
}
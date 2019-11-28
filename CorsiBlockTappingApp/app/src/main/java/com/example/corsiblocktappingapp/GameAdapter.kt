package com.example.corsiblocktappingapp

import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(private val dataset: Array<String>): RecyclerView.Adapter<GameAdapter.GameViewHolder>(){
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    class GameViewHolder(val blockButton: Button) : RecyclerView.ViewHolder(blockButton){
        var clickedOn:Boolean
        val bg = blockButton.backgroundTintList
        init {
            clickedOn = false
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameAdapter.GameViewHolder {
        val corsiButton = LayoutInflater.from(parent.context).inflate(R.layout.corsi_button, parent, false) as Button

        return GameViewHolder(corsiButton)
    }

    override fun getItemCount(): Int {
        return dataset.size //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: GameAdapter.GameViewHolder, position: Int) {
        holder.blockButton.text = dataset[position] //To change body of created functions use File | Settings | File Templates.
    }


}
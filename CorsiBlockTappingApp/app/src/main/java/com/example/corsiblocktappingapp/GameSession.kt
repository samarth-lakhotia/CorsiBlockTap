package com.example.corsiblocktappingapp

import java.time.LocalDateTime

class GameSession(var difficulty: DIFFICULTY){
    private lateinit var rounds: ArrayList<TappingRound>
    private lateinit var totalTime: LocalDateTime

}
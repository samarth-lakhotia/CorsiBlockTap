package com.example.corsiblocktappingapp

import java.time.LocalDateTime

class TappingRound(roundNumber: Int, blocksToRemember: Int, patternToRemember: HashSet<Int>) {
    private var roundNumber = roundNumber
    private var numBlocksToRemember = blocksToRemember
    private var patternToRemember = patternToRemember
    private var iterator = patternToRemember.iterator()
    private var timestamps: ArrayList<LocalDateTime> = ArrayList()
    private var numTries =1
    var correctlyEntered = false
    fun stampIt() {
        timestamps.add(LocalDateTime.now())
    }


    fun getTimestamps(): ArrayList<LocalDateTime> {
        return timestamps
    }
}
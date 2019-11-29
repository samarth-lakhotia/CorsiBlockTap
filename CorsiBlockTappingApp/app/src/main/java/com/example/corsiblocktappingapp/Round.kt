package com.example.corsiblocktappingapp

import java.time.LocalDateTime

class TappingRound(roundNumber: Int, blocksToRemember: Int, patternToRemember: HashSet<Int>) {
    private var roundNumber = roundNumber
    private var numBlocksToRemember = blocksToRemember
    private var patternToRemember = patternToRemember
    private var iterator = patternToRemember.iterator()
    private var timestamps: ArrayList<LocalDateTime> = ArrayList()
    var numTriesLeft =2
    private var totalTimeTaken: Long =0
    var correctlyEntered = false

    fun stampIt() {
        timestamps.add(LocalDateTime.now())
    }

    fun getTimestamps(): ArrayList<LocalDateTime> {
        return timestamps
    }

    fun endRound(mTimerTotal: Long) {
        totalTimeTaken=mTimerTotal
    }

    fun useTry():Boolean{
        if(numTriesLeft==0){
            return false
        }
        numTriesLeft--
        return true
    }

    fun getPatternToRemember(): HashSet<Int> {
        return patternToRemember
    }

}
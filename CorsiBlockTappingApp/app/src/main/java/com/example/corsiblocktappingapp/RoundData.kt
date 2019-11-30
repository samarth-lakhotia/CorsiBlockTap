package com.example.corsiblocktappingapp

class RoundData {
    var datetime: String? = null
    var tapPosition: Int? = null
    var elapsedRoundTimeInSec: String? = null
    var currentBlocksToRmr: Int? = null
    var correct: Boolean? = null

    constructor() {}
    constructor(datetime: String?, tapPosition: Int?, elapsedRoundTimeInSec: String?, currentBlocksToRmr: Int?, correct: Boolean?) {
        this.datetime = datetime
        this.tapPosition = tapPosition
        this.elapsedRoundTimeInSec = elapsedRoundTimeInSec
        this.currentBlocksToRmr = currentBlocksToRmr
        this.correct = correct
    }

    override fun toString(): String {
        return "RoundData [datetime=" + datetime + ", tapPosition=" + tapPosition + ", elapsedRoundTimeInSec=" + elapsedRoundTimeInSec + ", currentBlocksToRmr=" + currentBlocksToRmr + ", correct=" + correct + "]"
    }
}
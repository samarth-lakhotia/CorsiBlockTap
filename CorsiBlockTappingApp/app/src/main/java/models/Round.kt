package models

import android.annotation.SuppressLint
import java.time.LocalDateTime

class Round(roundNumber: Int, blocksToRemember: Int, patternToRemember: HashSet<Int>) {
    private var roundNumber = roundNumber
    private var numBlocksToRemember = blocksToRemember


    private var patternToRemember = patternToRemember
    private var userTaps= ArrayList<BlockTap>()

    private var iterator = patternToRemember.iterator()
    var numTriesLeft =2
    private var totalTimeTaken: Long =0
    var allBlocksWereCorrectlyEntered = false

    @SuppressLint("NewApi")
    fun timeStampIt(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun endRound(mTimerTotal: Long) {
        totalTimeTaken=mTimerTotal
    }

    fun useTry():Boolean{
        if(numTriesLeft==0){
            return false
        }
        numTriesLeft--
        iterator = patternToRemember.iterator()
        return true
    }

    fun getPatternToRemember(): HashSet<Int> {
        return patternToRemember
    }

    fun addTap(tapPosition: Int, elapsedTime:Long): Boolean{
        val block= BlockTap(
            tapPosition,
            timeStampIt(),
            elapsedTime
        )
        userTaps.add(block)
        if(iterator.hasNext() && iterator.next() == tapPosition) {
            if(!iterator.hasNext()){
                allBlocksWereCorrectlyEntered=true
            }
            return true
        }
        block.setCorrectness(false)
        return false
    }

    fun areThereTriesLeft():Boolean{
        return numTriesLeft > 0
    }
}
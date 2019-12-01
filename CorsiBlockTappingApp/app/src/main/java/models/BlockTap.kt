package models

import java.io.Serializable
import java.time.LocalDateTime

class BlockTap(position:Int, tapTime:LocalDateTime, elapsedTime:Long) : Serializable {
    var tapTimestamp: LocalDateTime = tapTime
    var tapPositionWithRespectToGrid: Int? = position
    var timeTappedSinceBeginning: Long = elapsedTime
    var wasItCorrectlyTapped: Boolean = true

    fun setCorrectness(value:Boolean){
        wasItCorrectlyTapped=value
    }

    override fun toString(): String {
        return "RoundData [datetime=" + tapTimestamp + ", tapPosition=" + tapPositionWithRespectToGrid + ", elapsedRoundTimeInSec=" + timeTappedSinceBeginning +", correct=" + wasItCorrectlyTapped + "]"
    }
}
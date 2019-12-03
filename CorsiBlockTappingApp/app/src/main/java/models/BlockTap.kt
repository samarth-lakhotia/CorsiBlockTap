package models

import java.io.Serializable
import java.time.LocalDateTime

class BlockTap(position:Int, tapTime:LocalDateTime, elapsedTime:Long) : Serializable {
    var tapTimestamp: LocalDateTime = tapTime
    var tapPositionWithRespectToGrid: Int? = position
    var timeTappedSinceBeginning: Long = elapsedTime
    var wasItCorrectlyTapped: Boolean = true
    var uuid: String = ""

    fun setCorrectness(value:Boolean){
        wasItCorrectlyTapped=value
    }

    override fun toString(): String {
        return "Tap [Time Stamped On=" + tapTimestamp + "; Position tapped on=" + tapPositionWithRespectToGrid +"; Seconds passed since beginning=" + timeTappedSinceBeginning +"; Correctness of Tap=" + wasItCorrectlyTapped + "]"
    }
}
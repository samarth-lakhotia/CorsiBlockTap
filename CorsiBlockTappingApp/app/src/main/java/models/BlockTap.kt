package models

import org.json.JSONObject
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
        return """{"Time Stamped On":$tapTimestamp, Position Tapped On:$tapPositionWithRespectToGrid, Time(in ms) since beginning of round:${timeTappedSinceBeginning}, Correct Tap?:${wasItCorrectlyTapped}}"""
    }

    fun toJSON(): JSONObject {
        var jsonObject = JSONObject()
        jsonObject.put("Time Stamped On",tapTimestamp.toString())
        jsonObject.put("Position Tapped On", tapPositionWithRespectToGrid)
        jsonObject.put("Elapsed Time(ms)", timeTappedSinceBeginning)
        jsonObject.put("Correctly Tapped", wasItCorrectlyTapped)
        return jsonObject
    }
}
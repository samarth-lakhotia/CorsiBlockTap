package models

import android.annotation.SuppressLint
import com.example.corsiblocktappingapp.Constants
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.time.LocalDateTime

class Round(roundNumber: Int, blocksToRemember: Int, patternToRemember: HashSet<Int>): Serializable {
    private var roundNumber = roundNumber
    private var numBlocksToRemember = blocksToRemember


    private var patternToRemember = patternToRemember
    private var userTaps = ArrayList<ArrayList<BlockTap>>()

    private var iterator = patternToRemember.iterator()
    var numTriesLeft = Constants.NUMBER_OF_TRIES
    private var totalTimeTaken: Long = 0
    var allBlocksWereCorrectlyEntered = false

    @SuppressLint("NewApi")
    fun timeStampIt(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun getUserTaps(): ArrayList<ArrayList<BlockTap>> {
        return userTaps
    }

    fun endRound(mTimerTotal: Long) {
        totalTimeTaken = mTimerTotal
    }

    fun useTry(): Boolean {
        if (numTriesLeft == 0) {
            return false
        }
        numTriesLeft--
        iterator = patternToRemember.iterator()
        userTaps.add(ArrayList())
        return true
    }

    fun getPatternToRemember(): HashSet<Int> {
        return patternToRemember
    }

    fun addTap(tapPosition: Int, elapsedTime: Long): Boolean {
        val block = BlockTap(
            tapPosition,
            timeStampIt(),
            elapsedTime
        )
        userTaps.last().add(block)
        if (iterator.hasNext() && iterator.next() == tapPosition) {
            if (!iterator.hasNext()) {
                allBlocksWereCorrectlyEntered = true
            }
            return true
        }
        block.setCorrectness(false)
        return false
    }

    fun areThereTriesLeft(): Boolean {
        return numTriesLeft > 0
    }

    override fun toString(): String {
        var roundDetails = ""
        userTaps.forEach{ roundDetails+=it.toString()}
        return """{"Round Number":"${roundNumber}","Number of Blocks to memorize":"${numBlocksToRemember}","Tries Used":"${Constants.NUMBER_OF_TRIES-numTriesLeft}","Tap Details":"${roundDetails}","Total Time Taken":"${totalTimeTaken}"}"""
    }
    fun toJSON(): JSONObject {
        var jsonObject=JSONObject()
        var tapsJSONArray = JSONArray()
        userTaps.forEach {
            var jsonArr=JSONArray()
            it.forEach { tap -> jsonArr.put(tap.toJSON()) }
            tapsJSONArray.put(jsonArr)
        }
        jsonObject.put("Round Number",roundNumber)
        jsonObject.put("Number of Blocks to memorize", numBlocksToRemember)
        jsonObject.put("Tries Used",Constants.NUMBER_OF_TRIES-numTriesLeft)
        jsonObject.put("Total time Taken", totalTimeTaken)
        jsonObject.put("Tap Details",tapsJSONArray)
        return jsonObject
    }
}
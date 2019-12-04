package models

import com.example.corsiblocktappingapp.DIFFICULTY
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class GameSession(difficulty: DIFFICULTY): Serializable{
    private var rounds: ArrayList<Round> = ArrayList()
    private var totalTime: Long? = null
    private var difficultyOfGame=difficulty
    private var currNumberOfBlocksToRemember=difficultyOfGame.INITIAL_BLOCKS_TO_REMEMBER

    fun addRound(pattern: HashSet<Int>){
        var round = Round(
            rounds.size+1,
            currNumberOfBlocksToRemember,
            pattern
        )
        rounds.add(round)
    }

    fun getInitialNumberOfBlocksToRemember():Int{
        return currNumberOfBlocksToRemember
    }
    fun getTheLatestRound(): Round {
        return rounds.last()
    }

    fun getRounds(): ArrayList<Round> {
        return rounds
    }

    fun tryRound(){
        getTheLatestRound().useTry()
    }
    fun getNumberOfTriesLeftForCurrentRound(): Int {
        return getTheLatestRound().numTriesLeft
    }

    fun checkIfUserWonCurrentRound(): Boolean {
        return getTheLatestRound().allBlocksWereCorrectlyEntered
    }

    fun getTheLatestPatternToRemember(): HashSet<Int> {
        return getTheLatestRound().getPatternToRemember()
    }
    fun endTheCurrentRound(elapsedTime:Long){
        getTheLatestRound().endRound(elapsedTime)
    }

    fun addTap(position:Int, milliRoundTime:Long): Boolean {
        return getTheLatestRound().addTap(position, milliRoundTime)
    }
    fun endSession(totalTimeTaken: Long){
        totalTime = totalTimeTaken
    }

    fun getNumberOfRoundsPlayed(): Int {
        return rounds.size
    }

    override fun toString(): String {
        var roundsDetails = ""
        rounds.forEach {
            roundsDetails+=it.toString()
        }
        return """${getNumberOfRoundsPlayed()},${difficultyOfGame},${roundsDetails},${totalTime}"""
    }
    fun toJSON(): JSONObject {
        var jsonObject = JSONObject()
        var jsonArray = JSONArray()
        jsonObject.put("Number of Rounds Played",getNumberOfRoundsPlayed())
        jsonObject.put("Total Time Taken", totalTime)
        rounds.forEach {
            jsonArray.put(it.toJSON())
        }
        jsonObject.put("Rounds", jsonArray)
        return jsonObject
    }
}
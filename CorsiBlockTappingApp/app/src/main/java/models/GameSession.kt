package models

import com.example.corsiblocktappingapp.DIFFICULTY

class GameSession(difficulty: DIFFICULTY){
    private var rounds: ArrayList<Round> = ArrayList()
    private var totalTime: Long? = null
    private var difficultyOfGame=difficulty
    private var currNumberOfBlocksToRemember=difficultyOfGame.INITIAL_BLOCKS_TO_REMEMBER

    fun addRound(pattern: HashSet<Int>){
        var round = Round(
            rounds.size,
            currNumberOfBlocksToRemember,
            pattern
        )
        rounds.add(round)
    }

    fun getTheLatestRound(): Round {
        return rounds.last()
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
}
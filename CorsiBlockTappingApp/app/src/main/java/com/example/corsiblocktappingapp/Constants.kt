package com.example.corsiblocktappingapp

enum class DIFFICULTY {
    EASY_DIFFICULTY {
        override var INITIAL_BLOCKS_TO_REMEMBER = 1
        override var id=0
        override var NUMBER_OF_BLOCKS = 20
    },
    MEDIUM_DIFFICULTY {
        override var INITIAL_BLOCKS_TO_REMEMBER = 1
        override var id=1
        override var NUMBER_OF_BLOCKS = 30
    },
    HARD_DIFFICULTY {
        override var INITIAL_BLOCKS_TO_REMEMBER = 1
        override var id=2
        override var NUMBER_OF_BLOCKS = 25
    };

    abstract var INITIAL_BLOCKS_TO_REMEMBER: Int
    abstract var id:Int
    abstract var NUMBER_OF_BLOCKS: Int
}

class Constants {
    companion object{
        const val COLS_IN_GRID=5
        const val NUMBER_OF_TRIES=2
    }

}


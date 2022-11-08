package com.example.firstappcompose

data class GameUiState(
    val guessString: String,
    val madeCorrectGuess: Boolean,
    val guessRange: LongRange,
    val canPlayAgain: Boolean = true

)
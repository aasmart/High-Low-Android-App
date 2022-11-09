package com.example.firstappcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    init {
        pickRandomNumber(0L..100L)
    }

    private val _uiState = MutableStateFlow(GameUiState("Current guesses = 0", false, 0L..100L))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var currentNumber: Long = 0
    private var guessCount: Int = 0
    var userGuess by mutableStateOf("")
        private set

    var lowerBound by mutableStateOf("0")
        private set

    var upperBound by mutableStateOf("100")
        private set

    /**
     * Sets the random number guess based on a random number inside the given range
     * @param range The range of values to generate the number between
     */
    private fun pickRandomNumber(range: LongRange) {
        currentNumber = range.shuffled().random().toLong()
    }

    /**
     * Returns true if the number meets the following conditions:
     * * Greater than zero
     * * A Long
     * * Not null
     *
     * @param number The number to check
     */
    fun isValidNumber(number: String): Boolean {
        val num: Long? = try { number.toLong() } catch (e: java.lang.NumberFormatException) { null }

        if(num == null || num < 0)
            return false

        return true
    }

    /**
     * Takes a String and updates the user's guess
     */
    fun updateGuess(guess: String) {
        userGuess = guess
        _uiState.value = GameUiState(guessString = "Current guesses = $guessCount", false, _uiState.value.guessRange)
    }

    /**
     * Takes a String and updates the RNG lower bound to said value
     */
    fun updateLower(lower: String) {
        this.lowerBound = lower
        canPlayAgain()
    }

    /**
     * Takes a String and updates the RNG upper bound to said value
     */
    fun updateUpper(upper: String) {
        this.upperBound = upper
        canPlayAgain()
    }

    /**
     * Checks if the current range the number is generated between is valid
     * @return True if the both range values are [valid numbers][isValidNumber] and
     *         the lower bound is less than the upper bound
     */
    fun isValidRange(): Boolean {
        if(!isValidNumber(upperBound) || !isValidNumber(lowerBound))
            return false

        if(upperBound.toLong() < lowerBound.toLong())
            return false

        return true
    }

    /**
     * Converts the lower and upper bounds into a range
     * @return The value range
     */
    private fun getBoundsAsRange(): LongRange {
        return lowerBound.toLong()..(upperBound.toLong())
    }

    /**
     * Determines if the user can hit the "Play Again" button. If the player has entered invalid number bounds,
     * then they cannot play again.
     */
    private fun canPlayAgain() {
        _uiState.value =
            GameUiState(_uiState.value.guessString, true, _uiState.value.guessRange, isValidRange())
    }

    /**
     * Submits the user's guess
     */
    fun makeGuess() {
        if(!isValidNumber(userGuess))
            return

        guessCount++

        val guess: String
        var correct = false
        if(userGuess.toLong() < currentNumber)
            guess = "Your guess of $userGuess is too low!"
        else if(userGuess.toLong() > currentNumber)
            guess = "Your guess of $userGuess is too high!"
        else {
            guess = "Your guess of $userGuess is correct! It took you $guessCount ${if(guessCount == 1) "guess" else "guesses"}."
            correct = true
        }

        // Update the UI
        _uiState.value = GameUiState(guessString = guess, correct, _uiState.value.guessRange)
    }

    /**
     * Restarts the game
     */
    fun restart() {
        pickRandomNumber(getBoundsAsRange())
        guessCount = 0
        userGuess = ""
        _uiState.value = GameUiState(guessString = "Current guesses = 0", false, lowerBound.toLong()..upperBound.toLong())
    }
}
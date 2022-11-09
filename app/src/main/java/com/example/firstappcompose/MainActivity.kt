package com.example.firstappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firstappcompose.ui.theme.FirstAppComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstAppComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}

/**
 * Creates the main game screen displayed to the player
 *
 * @param modifier Modifiers
 * @param gameViewModel The view model the game interacts with
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleText()
        GuessBar(
            guess = gameViewModel.userGuess,
            guessRange = gameUiState.guessRange,
            valueChange = { gameViewModel.updateGuess(it) },
            error = !gameViewModel.isValidNumber(gameViewModel.userGuess),
            madeCorrectGuess = gameUiState.madeCorrectGuess,
            done = { gameViewModel.makeGuess() }
        ) {
            gameViewModel.makeGuess()
        }

        GuessElement(
            guessString = gameUiState.guessString,
            gameUiState.madeCorrectGuess,
            canPlayAgain = gameUiState.canPlayAgain,
            playAgain = { gameViewModel.restart() })

        RangeSelectionBar(
            lower = gameViewModel.lowerBound,
            isValidRange = gameViewModel.isValidRange(),
            upper = gameViewModel.upperBound,
            updateLower = { gameViewModel.updateLower(it) },
            updateUpper = { gameViewModel.updateUpper(it) },
            visible = gameUiState.madeCorrectGuess
        )
    }
}

/**
 * The title text composable
 */
@Composable
fun TitleText() {
    Text(
        text = "High or Low",
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier.padding(24.dp)
    )
}

/**
 * The bar and button that allows the user to input their guess
 *
 * @param guess The string value of the guess that is displayed inside the text field
 * @param guessRange The range of values that the number is generated between
 * @param valueChange Called when the value inside the text field is changed
 * @param error If true, the text fields will display and error and the user
 *              won't be able to enter their guess
 * @param madeCorrectGuess If true, the user will not be able to change the value
 *                         inside the text field or submit a new guess
 * @param done Called when the user presses the done button on the keyboard
 * @param click Called when the user clicks the "Guess" button
 */
@Composable
fun GuessBar(
    guess: String,
    guessRange: LongRange,
    valueChange: (String) -> Unit,
    error: Boolean,
    madeCorrectGuess: Boolean,
    done: KeyboardActionScope.() -> Unit,
    click: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
    ) {
        // The text field and error message
        Box() {
            OutlinedTextField(
                value = guess,
                onValueChange = valueChange,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = error,
                keyboardActions = KeyboardActions(onDone = done),
                label = { Text(text = "Enter Guess From ${guessRange.first}-${guessRange.last}") },
                readOnly = madeCorrectGuess,
                modifier = Modifier.padding(8.dp)
            )

            if (error) {
                Text(
                    text = "Invalid guess",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.offset(x = 24.dp, y = 75.dp)
                )
            }
        }

        // The button to submit the guess
        Button(
            onClick = click,
            enabled = !error && !madeCorrectGuess,
            modifier = Modifier
                .padding(8.dp)
                .height(60.dp)
                .offset(y = 6.dp)
        ) {
            Text(text = "Guess")
        }
    }
}

/**
 * The Composable to input the range that the values are generated between
 * @param visible True if the bar is visible
 * @param isValidRange True if the current range inputted is valid. If invalid, an error will appear
 * @param lower The string value of the lower bound that appears in the text field
 * @param updateLower Called when the value inside the lower bound text field changes
 * @param upper The string value of the upper bound that appears in the text field
 * @param updateUpper Called when the value inside the lower bound text field changes
 */
@Composable
fun RangeSelectionBar(
    visible: Boolean,
    isValidRange: Boolean,
    lower: String, updateLower: (String) -> Unit,
    upper: String, updateUpper: (String) -> Unit
) {
    if (!visible)
        return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Title
        Text(
            text = "Random Number Range",
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        )

        // Range Text Fields
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = lower,
                onValueChange = updateLower,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                label = { Text(text = "Lower Bound") },
                isError = !isValidRange,
                modifier = Modifier
                    .padding(8.dp)
                    .width(180.dp),
            )

            OutlinedTextField(
                value = upper,
                onValueChange = updateUpper,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                label = { Text(text = "Upper Bound") },
                modifier = Modifier
                    .padding(8.dp)
                    .width(180.dp),
                isError = !isValidRange,
            )
        }

        // Error message
        if (!isValidRange) {
            Text(
                text = "Make sure the values are positive and the lower bound is greater than the upper bound.",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Displays the guess feedback text and the button that allows the user to
 * play again if their guess is correct
 *
 * @param guessString The guess feedback text displayed in the textbox
 * @param madeCorrectGuess If the user has made a correct guess. If true, the "Play Again" button will be displayed
 * @param canPlayAgain If true, the user may click the "Play Again" button.
 * @param playAgain Called when the user clicks the "Play Again" button
 */
@Composable
fun GuessElement(
    guessString: String,
    madeCorrectGuess: Boolean,
    canPlayAgain: Boolean,
    playAgain: () -> Unit
) {
    // Guess feedback text
    Text(
        text = guessString,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(24.dp)
    )

    // Display the "Play Again" button if the user has made a correct guess
    if (madeCorrectGuess)
        Button(
            onClick = playAgain,
            modifier = Modifier.offset(y = (-24).dp),
            enabled = canPlayAgain
        ) {
            Text(text = "Play Again")
        }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FirstAppComposeTheme {

    }
}


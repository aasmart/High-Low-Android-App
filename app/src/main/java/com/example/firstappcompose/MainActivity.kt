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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

        GuessText(guessString = gameUiState.guessString, gameUiState.madeCorrectGuess, canPlayAgain = gameUiState.canPlayAgain, playAgain = { gameViewModel.restart() })

        RangeSelectionBar(
            lower = gameViewModel.lowerBound,
            isValidRange = gameViewModel.isValidRange(),
            upper = gameViewModel.upperBound,
            updateLower = { gameViewModel.updateLower(it) },
            updateUpper = { gameViewModel.updateUpper(it) },
            canModify = gameUiState.madeCorrectGuess
        )
    }
}

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

            if(error) {
                Text(
                    text = "Invalid guess",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.offset(x = 24.dp, y = 75.dp)
                )
            }
        }

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

@Composable
fun RangeSelectionBar(
    canModify: Boolean,
    isValidRange: Boolean,
    lower: String, updateLower: (String) -> Unit,
    upper: String, updateUpper: (String) -> Unit
) {
    if(!canModify)
        return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Random Number Range",
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        )
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = lower,
                onValueChange = updateLower,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                label = { Text(text = "Enter Lower Bound") },
                isError = !isValidRange,
                modifier = Modifier.padding(8.dp).width(180.dp),
            )

            OutlinedTextField(
                value = upper,
                onValueChange = updateUpper,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                label = { Text(text = "Enter Upper Bound") },
                modifier = Modifier.padding(8.dp).width(180.dp),
                isError = !isValidRange,
            )
        }

        if(!isValidRange) {
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

@Composable
fun GuessText(guessString: String, madeCorrectGuess: Boolean, canPlayAgain: Boolean, playAgain: () -> Unit) {
    Text(
        text = guessString,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(24.dp)
    )

    if(madeCorrectGuess)
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


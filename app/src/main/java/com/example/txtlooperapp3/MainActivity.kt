package com.example.txtlooperapp3

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.txtlooperapp3.ui.theme.TxtLooperApp3Theme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set status bar color to light color
        window.statusBarColor = android.graphics.Color.parseColor("#FFFFFF")
        // Make status bar icons dark for better visibility against light background
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // This makes the icons dark
        }
        
        setContent {
            TxtLooperApp3Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFF5F5F5)
                ) { paddingValues ->
                    TextLooperScreen(Modifier.padding(paddingValues))
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TextLooperScreen(modifier: Modifier = Modifier) {
    var loopNumber by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Animation states
    val buttonScale = remember { Animatable(1f) }
    var isResultVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated text fields
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = loopNumber,
                        onValueChange = { 
                            // Add number validation
                            val number = it.toIntOrNull() ?: 0
                            if (it.isEmpty() || (number in 1..100)) {
                                loopNumber = it
                            }
                        },
                        label = { Text("Enter number of loops (1-100)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color.Black,
                            focusedTextColor = Color.Black
                        )
                    )

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Enter text to be looped ") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color.Black,
                            focusedTextColor = Color.Black
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            buttonScale.animateTo(
                                0.8f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            )
                            buttonScale.animateTo(
                                1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            )
                        }
                        
                        val number = loopNumber.toIntOrNull() ?: 0
                        val result = StringBuilder()
                        for (i in 1..number) {
                            result.append(inputText)
                            if (i < number) result.append("\n")
                        }
                        resultText = result.toString()
                        isResultVisible = true
                    },
                    modifier = Modifier.scale(buttonScale.value),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("OK")
                }

                Button(
                    onClick = {
                        loopNumber = ""
                        inputText = ""
                        resultText = ""
                        isResultVisible = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                ) {
                    Text("Reset")
                }
            }

            // Animated result surface with scroll
            AnimatedVisibility(
                visible = isResultVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = resultText,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Animated copy button - Now outside the scrollable area
        AnimatedVisibility(
            visible = resultText.isNotEmpty(),
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Button(
                onClick = {
                    if (resultText.isNotEmpty()) {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Looped Text", resultText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
            ) {
                Text("Copy")
            }
        }
    }
}




package com.example.calculatorapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder

@Composable
fun CalculatorUi() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }

    val onButtonClick: (String) -> Unit = { value ->
        when (value) {
            "C" -> {
                input = ""
                result = "0"
            }
            "⌫" -> input = input.dropLast(1)
            "%" -> {
                try {
                    result = (result.toDoubleOrNull()?.div(100)).toString()
                } catch (e: Exception) {
                    result = "Error"
                }
            }
            "√" -> {
                try {
                    result = kotlin.math.sqrt(result.toDoubleOrNull() ?: 0.0).toString()
                } catch (e: Exception) {
                    result = "Error"
                }
            }
            "=" -> {
                try {
                    result = evalExpression(input)
                    history = history + "$input = $result"
                } catch (e: Exception) {
                    result = "Error"
                }
            }
            else -> input += value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        // Title Bar
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E2E2E))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Build,
                contentDescription = "Calculator Icon",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CalculatorApp",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Area
        Text(
            text = input.ifEmpty { "0" },
            fontSize = 48.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Text(
            text = result,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(16.dp))

        // History Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.DarkGray)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            history.forEach {
                Text(text = it, color = Color.White, fontSize = 16.sp)
            }
        }

        // Clear History Button
        Button(
            onClick = { history = emptyList() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Clear History")
        }

        // Button Layout
        val buttons = listOf(
            listOf("C", "⌫", "%", "√"),
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", "=", ".", "+")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { label ->
                    CalculatorButton(label, onClick = { onButtonClick(label) })
                }
            }
        }
    }
}

// Button with Animation
@Composable
fun RowScope.CalculatorButton(label: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    val buttonColor = when (label) {
        "C", "⌫", "=" -> Color.Red
        "/", "*", "-", "+" -> Color(0xFF4CAF50) // Green for operators
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(4.dp)
            .scale(scale)
            .background(buttonColor, shape = MaterialTheme.shapes.medium)
            .clickable {
                pressed = true
                onClick()
                pressed = false
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 24.sp, color = Color.White)
    }
}

fun evalExpression(expression: String): String {
    return try {
        val result = ExpressionBuilder(expression).build().evaluate()
        if (result % 1 == 0.0) result.toInt().toString()
        else result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalculatorPreview() {
    CalculatorUi()
}

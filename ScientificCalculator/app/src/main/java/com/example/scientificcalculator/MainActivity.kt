package com.example.scientificcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.cos
import java.lang.Math.log
import java.lang.Math.log10
import java.lang.Math.pow
import java.lang.Math.sin
import java.lang.Math.tan
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var inputString: String = ""
    private var currentResult: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        val buttons = getAllButtons()
        buttons.forEach { button ->
            button.setOnClickListener {
                val buttonText = (it as Button).text.toString()
                processButtonInput(buttonText)
            }
        }
    }

    private fun getAllButtons(): List<Button> {
        val buttonList = mutableListOf<Button>()
        val gridLayout = findViewById<android.widget.GridLayout>(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is Button) {
                buttonList.add(child)
            }
        }
        return buttonList
    }

    private fun processButtonInput(buttonText: String) {
        when (buttonText) {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "." -> {
                inputString += buttonText
                tvDisplay.text = inputString
            }
            "+", "-", "x", "÷" -> {
                inputString += buttonText
                tvDisplay.text = inputString
            }
            "AC" -> {
                inputString = ""
                tvDisplay.text = "0"
            }
            "Back" -> {
                if (inputString.isNotEmpty()) {
                    inputString = inputString.substring(0, inputString.length - 1)
                    tvDisplay.text = if (inputString.isEmpty()) "0" else inputString
                }
            }
            "=" -> {

                try {
                    val result = evaluateExpression(inputString)
                    currentResult = result
                    tvDisplay.text = result.toString()
                    inputString = result.toString()
                } catch (e: Exception) {
                    tvDisplay.text = "Error"
                    inputString = ""
                }
            }
            "sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹", "log", "ln", "√x", "x²" -> {
                inputString += "$buttonText("
                tvDisplay.text = inputString
            }
            "π" -> {
                inputString += "3.14159265359"
                tvDisplay.text = inputString
            }
            "e" -> {
                inputString += "2.71828182846"
                tvDisplay.text = inputString
            }
            "%" -> {
                inputString += "/100"
                tvDisplay.text = inputString
            }
            "xʸ" -> {
                inputString += "^"
                tvDisplay.text = inputString
            }
            "x³" -> {
                inputString += "^3"
                tvDisplay.text = inputString
            }
            "eˣ" -> {
                inputString += "e^"
                tvDisplay.text = inputString
            }
            "10ˣ" -> {
                inputString += "10^"
                tvDisplay.text = inputString
            }
            "³√x" -> {
                inputString += "cbrt("
                tvDisplay.text = inputString
            }
            "1/x" -> {
                inputString += "1/"
                tvDisplay.text = inputString
            }
            "n!" -> {
                inputString += "!"
                tvDisplay.text = inputString
            }
            "Ans" -> {
                inputString += currentResult.toString()
                tvDisplay.text = inputString
            }
            "(" -> {
                inputString += "("
                tvDisplay.text = inputString
            }
            ")" -> {
                inputString += ")"
                tvDisplay.text = inputString
            }

        }
    }


    private fun evaluateExpression(expression: String): Double {
        val expr = expression.replace("x", "*").replace("÷", "/")

        return eval(expr)
    }

    private fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = 0.toChar()
            fun nextChar() {
                ch = if (++pos < str.length) str[pos] else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch)
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+')) x += parseTerm()
                    else if (eat('-')) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*')) x *= parseFactor()
                    else if (eat('/')) x /= parseFactor()
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor()
                if (eat('-')) return -parseFactor()

                var x: Double
                val startPos = this.pos
                if (eat('(')) {
                    x = parseExpression()
                    eat(')')
                } else if (ch in '0'..'9' || ch == '.') {
                    while (ch in '0'..'9' || ch == '.') nextChar()
                    x = str.substring(startPos, this.pos).toDouble()
                } else if (ch in 'a'..'z') {
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    x = when (func) {
                        "sin" -> sin(Math.toRadians(x))
                        "cos" -> cos(Math.toRadians(x))
                        "tan" -> tan(Math.toRadians(x))
                        "sqrt" -> sqrt(x)
                        "log" -> log10(x)
                        "ln" -> log(x)
                        else -> throw RuntimeException("Unknown function: " + func)
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch)
                }

                if (eat('^')) x = pow(x, parseFactor())
                while (eat('!')) {
                    x = factorial(x)
                }
                return x
            }
            private fun factorial(n: Double): Double {
                val num = n.toInt()
                if (num < 0) throw RuntimeException("Negative factorial")
                var result = 1.0
                for (i in 1..num) {
                    result *= i
                }
                return result
            }

        }.parse()
    }
}
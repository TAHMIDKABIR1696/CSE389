package com.example.vangtichai

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentInput: String = ""

    private lateinit var tvAmount: TextView
    private lateinit var tvNote500: TextView
    private lateinit var tvNote100: TextView
    private lateinit var tvNote50: TextView
    private lateinit var tvNote20: TextView
    private lateinit var tvNote10: TextView
    private lateinit var tvNote5: TextView
    private lateinit var tvNote2: TextView
    private lateinit var tvNote1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvAmount = findViewById(R.id.tvAmount)
        tvNote500 = findViewById(R.id.tvNote500)
        tvNote100 = findViewById(R.id.tvNote100)
        tvNote50 = findViewById(R.id.tvNote50)
        tvNote20 = findViewById(R.id.tvNote20)
        tvNote10 = findViewById(R.id.tvNote10)
        tvNote5 = findViewById(R.id.tvNote5)
        tvNote2 = findViewById(R.id.tvNote2)
        tvNote1 = findViewById(R.id.tvNote1)

        if (savedInstanceState != null) {
            currentInput = savedInstanceState.getString("KEY_INPUT", "")
        }

        val btn0: Button = findViewById(R.id.btn0)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)
        val btnClear: Button = findViewById(R.id.btnClear)

        val digitButtons = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)
        for (button in digitButtons) {
            button.setOnClickListener {
                appendDigit(button.text.toString())
            }
        }

        btnClear.setOnClickListener {
            currentInput = ""
            updateUI()
        }

        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("KEY_INPUT", currentInput)
    }

    private fun appendDigit(digit: String) {
        if (currentInput == "0") {
            currentInput = digit
        } else if (currentInput.length < 9) {
            currentInput += digit
        }
        updateUI()
    }

    private fun updateUI() {
        if (currentInput.isEmpty()) {
            tvAmount.text = "Taka:"
            calculateAndDisplayChange(0L)
        } else {
            tvAmount.text = "Taka: $currentInput"
            val amount = currentInput.toLongOrNull() ?: 0L
            calculateAndDisplayChange(amount)
        }
    }

    private fun calculateAndDisplayChange(amount: Long) {
        var remaining = amount

        val count500 = remaining / 500
        remaining %= 500
        val count100 = remaining / 100
        remaining %= 100
        val count50 = remaining / 50
        remaining %= 50
        val count20 = remaining / 20
        remaining %= 20
        val count10 = remaining / 10
        remaining %= 10
        val count5 = remaining / 5
        remaining %= 5
        val count2 = remaining / 2
        remaining %= 2
        val count1 = remaining

        tvNote500.text = "500: $count500"
        tvNote100.text = "100: $count100"
        tvNote50.text = "50: $count50"
        tvNote20.text = "20: $count20"
        tvNote10.text = "10: $count10"
        tvNote5.text = "5: $count5"
        tvNote2.text = "2: $count2"
        tvNote1.text = "1: $count1"
    }
}
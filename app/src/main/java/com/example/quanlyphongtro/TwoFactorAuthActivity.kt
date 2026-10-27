package com.example.quanlyphongtro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class TwoFactorAuthActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_factor_auth)

        val username = intent.getStringExtra("username") ?: "admin"
        val etPin = findViewById<EditText>(R.id.etSecurityPin)
        val btnVerify = findViewById<Button>(R.id.btnVerifyPin)
        val btnBack = findViewById<Button>(R.id.btnTwoFactorBack)

        btnVerify.setOnClickListener {
            val pin = etPin.text.toString().trim()

            if (pin.isEmpty()) {
                etPin.error = "Please enter 2FA security PIN"
                etPin.requestFocus()
                return@setOnClickListener
            }

            // Default test 2FA PIN for admin is 888888
            if (pin == "888888") {
                Toast.makeText(this, "2FA Verification Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AdminActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid 2FA Security PIN (Hint: 888888)", Toast.LENGTH_LONG).show()
                etPin.error = "Incorrect PIN"
                etPin.requestFocus()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}

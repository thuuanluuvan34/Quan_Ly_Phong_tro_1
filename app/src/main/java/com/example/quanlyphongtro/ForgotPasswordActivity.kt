package com.example.quanlyphongtro

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ForgotPasswordActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val dbHelper = UserDatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etResetUsername)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnSubmit = findViewById<Button>(R.id.btnResetSubmit)
        val btnBack = findViewById<Button>(R.id.btnResetBack)

        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()

            if (username.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter username and new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.updatePassword(username, newPassword)
            if (success) {
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "User not found or update failed", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}

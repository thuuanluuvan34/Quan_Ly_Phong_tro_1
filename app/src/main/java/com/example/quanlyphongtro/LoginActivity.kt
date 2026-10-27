package com.example.quanlyphongtro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val dbHelper = UserDatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnForgotPassword = findViewById<Button>(R.id.btnForgotPassword)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cursor = dbHelper.authenticateUser(username, password)
            if (cursor != null) {
                val role = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ROLE))
                cursor.close()

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                if (role == "ADMIN") {
                    val intent = Intent(this, TwoFactorAuthActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, OwnerActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                }
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}

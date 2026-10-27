package com.example.quanlyphongtro

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val dbHelper = UserDatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etRegUsername)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etFullName = findViewById<EditText>(R.id.etRegFullName)
        val etPhone = findViewById<EditText>(R.id.etRegPhone)
        val btnSubmit = findViewById<Button>(R.id.btnRegisterSubmit)
        val btnBack = findViewById<Button>(R.id.btnBackLogin)

        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val fullName = etFullName.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.registerUser(username, password, "OWNER", fullName, phone)
            if (success) {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration failed. Username may already exist.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}

package com.example.quanlyphongtro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class AdminActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val username = intent.getStringExtra("username") ?: "Admin"
        findViewById<TextView>(R.id.txtAdminWelcome).text = "Welcome, $username (Admin)"

        findViewById<Button>(R.id.btnManageUsers).setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminStats).setOnClickListener {
            startActivity(Intent(this, AdminStatisticsActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

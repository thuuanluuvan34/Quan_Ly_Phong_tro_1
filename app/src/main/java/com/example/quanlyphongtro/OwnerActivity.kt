package com.example.quanlyphongtro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class OwnerActivity : Activity() {

    private lateinit var owner: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner)

        val username = intent.getStringExtra("username")
        if (username.isNullOrBlank()) {
            Toast.makeText(this, "Login information not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        owner = username
        findViewById<TextView>(R.id.txtOwnerWelcome).text = "Welcome, $owner!"

        fun openScreen(target: Class<out Activity>) {
            val screenIntent = Intent(this, target)
            screenIntent.putExtra("owner", owner)
            startActivity(screenIntent)
        }

        findViewById<Button>(R.id.btnRooms).setOnClickListener {
            openScreen(RoomsActivity::class.java)
        }
        findViewById<Button>(R.id.btnTenants).setOnClickListener {
            openScreen(TenantsActivity::class.java)
        }
        findViewById<Button>(R.id.btnElectric).setOnClickListener {
            openScreen(ElectricActivity::class.java)
        }
        findViewById<Button>(R.id.btnBills).setOnClickListener {
            openScreen(BillsActivity::class.java)
        }
        findViewById<Button>(R.id.btnContracts).setOnClickListener {
            openScreen(ContractsActivity::class.java)
        }
        findViewById<Button>(R.id.btnStatistics).setOnClickListener {
            openScreen(StatisticsActivity::class.java)
        }
        findViewById<Button>(R.id.btnOwnerLogout).setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
        }
    }
}

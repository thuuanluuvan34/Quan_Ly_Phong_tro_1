package com.example.quanlyphongtro

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class AdminStatisticsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_statistics)

        val dbHelper = UserDatabaseHelper(this)
        val cursor = dbHelper.getAllUsers()
        val count = cursor.count
        cursor.close()

        findViewById<TextView>(R.id.txtSystemStats).text = "Total Registered Users: $count"
    }
}

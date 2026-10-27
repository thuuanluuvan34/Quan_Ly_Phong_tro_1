package com.example.quanlyphongtro

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

open class StatisticsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val owner = intent.getStringExtra("owner") ?: ""
        val dbHelper = RoomDbHelper(this)

        val totalRevenue = dbHelper.getTotalRevenue(owner)
        val roomCounts = dbHelper.getRoomCounts(owner)

        findViewById<TextView>(R.id.txtTotalRevenue).text = "Total Revenue: $totalRevenue"
        findViewById<TextView>(R.id.txtRoomStats).text = "Rooms: ${roomCounts.first} Total | ${roomCounts.second} Occupied"
    }
}

package com.example.quanlyphongtro

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*

class RoomsActivity : Activity() {

    private lateinit var dbHelper: RoomDbHelper
    private lateinit var owner: String
    private lateinit var lvRooms: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        owner = intent.getStringExtra("owner") ?: ""
        dbHelper = RoomDbHelper(this)
        lvRooms = findViewById(R.id.lvRooms)

        findViewById<Button>(R.id.btnAddRoom).setOnClickListener {
            showRoomDialog(0L)
        }

        loadRooms()
    }

    private fun loadRooms() {
        val cursor = dbHelper.getRooms(owner)
        val list = ArrayList<String>()
        val ids = ArrayList<Long>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ID))
            val number = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ROOM_NUMBER))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ROOM_PRICE))
            val area = cursor.getDouble(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ROOM_AREA))
            val status = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ROOM_STATUS))

            list.add("Room: $number | Price: $price | Area: ${area}m² | Status: $status")
            ids.add(id)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvRooms.adapter = adapter

        lvRooms.setOnItemClickListener { _, _, position, _ ->
            val roomId = ids[position]
            val options = arrayOf("Edit", "Delete")
            AlertDialog.Builder(this)
                .setTitle("Room Options")
                .setItems(options) { _, which ->
                    if (which == 0) {
                        showRoomDialog(roomId)
                    } else {
                        dbHelper.deleteRoom(roomId)
                        Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show()
                        loadRooms()
                    }
                }
                .show()
        }
    }

    private fun showRoomDialog(roomId: Long) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val etNumber = EditText(this).apply { hint = "Room Number" }
        val etPrice = EditText(this).apply { hint = "Monthly Price"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val etArea = EditText(this).apply { hint = "Area (m²)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val etStatus = EditText(this).apply { hint = "Status (Vacant/Occupied)"; setText("Vacant") }
        val etDesc = EditText(this).apply { hint = "Description" }

        layout.addView(etNumber)
        layout.addView(etPrice)
        layout.addView(etArea)
        layout.addView(etStatus)
        layout.addView(etDesc)

        AlertDialog.Builder(this)
            .setTitle(if (roomId == 0L) "Add Room" else "Edit Room")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val num = etNumber.text.toString().trim()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val area = etArea.text.toString().toDoubleOrNull() ?: 0.0
                val status = etStatus.text.toString().trim()
                val desc = etDesc.text.toString().trim()

                if (num.isEmpty()) {
                    Toast.makeText(this, "Room number is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (roomId == 0L) {
                    dbHelper.addRoom(owner, num, price, area, status, desc)
                    Toast.makeText(this, "Room added", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.updateRoom(roomId, num, price, area, status, desc)
                    Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show()
                }
                loadRooms()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

package com.example.quanlyphongtro

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class AdminUsersActivity : Activity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        dbHelper = UserDatabaseHelper(this)

        loadUsers()
    }

    private fun loadUsers() {
        val lv = findViewById<ListView>(R.id.lvAdminUsers)
        val cursor = dbHelper.getAllUsers()
        val list = ArrayList<String>()
        val ids = ArrayList<Long>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_USERNAME))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ROLE))
            val status = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_STATUS))

            list.add("User: $username | Role: $role | Status: $status")
            ids.add(id)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lv.adapter = adapter

        lv.setOnItemClickListener { _, _, position, _ ->
            val userId = ids[position]
            AlertDialog.Builder(this)
                .setTitle("User Options")
                .setItems(arrayOf("Toggle Status (Active/Locked)", "Delete")) { _, which ->
                    if (which == 0) {
                        dbHelper.updateUserStatus(userId, "LOCKED")
                        Toast.makeText(this, "User status updated", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.deleteUser(userId)
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                    }
                    loadUsers()
                }
                .show()
        }
    }
}

package com.example.quanlyphongtro

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*

class ElectricActivity : Activity() {

    private lateinit var dbHelper: RoomDbHelper
    private lateinit var owner: String
    private lateinit var lvElectric: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electric)

        owner = intent.getStringExtra("owner") ?: ""
        dbHelper = RoomDbHelper(this)
        lvElectric = findViewById(R.id.lvElectric)

        findViewById<Button>(R.id.btnAddElectric).setOnClickListener {
            showElectricDialog()
        }

        loadElectricRecords()
    }

    private fun loadElectricRecords() {
        val cursor = dbHelper.getElectricRecords(owner)
        val list = ArrayList<String>()
        val ids = ArrayList<Long>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ID))
            val month = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ELEC_MONTH))
            val consumption = cursor.getInt(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ELEC_CONSUMPTION))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ELEC_TOTAL))

            list.add("Month: $month | Consumption: ${consumption}kWh | Total: $total")
            ids.add(id)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvElectric.adapter = adapter

        lvElectric.setOnItemClickListener { _, _, position, _ ->
            val id = ids[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Electric Record?")
                .setPositiveButton("Yes") { _, _ ->
                    dbHelper.deleteElectricRecord(id)
                    Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show()
                    loadElectricRecords()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun showElectricDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val etMonth = EditText(this).apply { hint = "Month (e.g. 2023-10)" }
        val etOld = EditText(this).apply { hint = "Old Index"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val etNew = EditText(this).apply { hint = "New Index"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val etPrice = EditText(this).apply { hint = "Price per kWh"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }

        layout.addView(etMonth)
        layout.addView(etOld)
        layout.addView(etNew)
        layout.addView(etPrice)

        AlertDialog.Builder(this)
            .setTitle("Add Electric Record")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val month = etMonth.text.toString().trim()
                val oldIdx = etOld.text.toString().toIntOrNull() ?: 0
                val newIdx = etNew.text.toString().toIntOrNull() ?: 0
                val price = etPrice.text.toString().toDoubleOrNull() ?: 3000.0

                dbHelper.addElectricRecord(owner, 1L, month, oldIdx, newIdx, price)
                Toast.makeText(this, "Electric record added", Toast.LENGTH_SHORT).show()
                loadElectricRecords()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

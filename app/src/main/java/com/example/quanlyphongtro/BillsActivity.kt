package com.example.quanlyphongtro

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*

class BillsActivity : Activity() {

    private lateinit var dbHelper: RoomDbHelper
    private lateinit var owner: String
    private lateinit var lvBills: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bills)

        owner = intent.getStringExtra("owner") ?: ""
        dbHelper = RoomDbHelper(this)
        lvBills = findViewById(R.id.lvBills)

        findViewById<Button>(R.id.btnAddBill).setOnClickListener {
            showBillDialog()
        }

        loadBills()
    }

    private fun loadBills() {
        val cursor = dbHelper.getBills(owner)
        val list = ArrayList<String>()
        val ids = ArrayList<Long>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ID))
            val month = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_BILL_MONTH))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_BILL_TOTAL))
            val status = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_BILL_STATUS))

            list.add("Month: $month | Total: $total | Status: $status")
            ids.add(id)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvBills.adapter = adapter

        lvBills.setOnItemClickListener { _, _, position, _ ->
            val billId = ids[position]
            AlertDialog.Builder(this)
                .setTitle("Bill Options")
                .setItems(arrayOf("Mark as Paid", "Delete")) { _, which ->
                    if (which == 0) {
                        dbHelper.updateBillStatus(billId, "Paid")
                        Toast.makeText(this, "Bill marked as paid", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.deleteBill(billId)
                        Toast.makeText(this, "Bill deleted", Toast.LENGTH_SHORT).show()
                    }
                    loadBills()
                }
                .show()
        }
    }

    private fun showBillDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val etMonth = EditText(this).apply { hint = "Month (e.g. 2023-10)" }
        val etRent = EditText(this).apply { hint = "Room Rent"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val etElectric = EditText(this).apply { hint = "Electric Cost"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val etWater = EditText(this).apply { hint = "Water Cost"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val etOther = EditText(this).apply { hint = "Other Cost"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }

        layout.addView(etMonth)
        layout.addView(etRent)
        layout.addView(etElectric)
        layout.addView(etWater)
        layout.addView(etOther)

        AlertDialog.Builder(this)
            .setTitle("Create Bill")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val month = etMonth.text.toString().trim()
                val rent = etRent.text.toString().toDoubleOrNull() ?: 0.0
                val electric = etElectric.text.toString().toDoubleOrNull() ?: 0.0
                val water = etWater.text.toString().toDoubleOrNull() ?: 0.0
                val other = etOther.text.toString().toDoubleOrNull() ?: 0.0

                dbHelper.addBill(owner, 1L, month, rent, electric, water, other, "Unpaid")
                Toast.makeText(this, "Bill created", Toast.LENGTH_SHORT).show()
                loadBills()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

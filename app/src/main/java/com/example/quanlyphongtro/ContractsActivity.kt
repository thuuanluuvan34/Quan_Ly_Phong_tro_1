package com.example.quanlyphongtro

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*

class ContractsActivity : Activity() {

    private lateinit var dbHelper: RoomDbHelper
    private lateinit var owner: String
    private lateinit var lvContracts: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contracts)

        owner = intent.getStringExtra("owner") ?: ""
        dbHelper = RoomDbHelper(this)
        lvContracts = findViewById(R.id.lvContracts)

        findViewById<Button>(R.id.btnAddContract).setOnClickListener {
            showContractDialog()
        }

        loadContracts()
    }

    private fun loadContracts() {
        val cursor = dbHelper.getContracts(owner)
        val list = ArrayList<String>()
        val ids = ArrayList<Long>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ID))
            val number = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_CONTRACT_NUMBER))
            val start = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_CONTRACT_START))
            val end = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_CONTRACT_END))

            list.add("Contract #: $number | From: $start To: $end")
            ids.add(id)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvContracts.adapter = adapter

        lvContracts.setOnItemClickListener { _, _, position, _ ->
            val id = ids[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Contract?")
                .setPositiveButton("Yes") { _, _ ->
                    dbHelper.deleteContract(id)
                    Toast.makeText(this, "Contract deleted", Toast.LENGTH_SHORT).show()
                    loadContracts()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun showContractDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val etNumber = EditText(this).apply { hint = "Contract Number" }
        val etStart = EditText(this).apply { hint = "Start Date (YYYY-MM-DD)" }
        val etEnd = EditText(this).apply { hint = "End Date (YYYY-MM-DD)" }
        val etDeposit = EditText(this).apply { hint = "Deposit Amount"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val etTerms = EditText(this).apply { hint = "Terms & Conditions" }

        layout.addView(etNumber)
        layout.addView(etStart)
        layout.addView(etEnd)
        layout.addView(etDeposit)
        layout.addView(etTerms)

        AlertDialog.Builder(this)
            .setTitle("Add Contract")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val number = etNumber.text.toString().trim()
                val start = etStart.text.toString().trim()
                val end = etEnd.text.toString().trim()
                val deposit = etDeposit.text.toString().toDoubleOrNull() ?: 0.0
                val terms = etTerms.text.toString().trim()

                dbHelper.addContract(owner, 1L, 1L, number, start, end, deposit, terms)
                Toast.makeText(this, "Contract added", Toast.LENGTH_SHORT).show()
                loadContracts()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

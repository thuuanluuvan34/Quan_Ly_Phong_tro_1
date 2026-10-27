package com.example.quanlyphongtro

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*

class TenantsActivity : Activity() {

    private lateinit var dbHelper: RoomDbHelper
    private lateinit var owner: String
    private lateinit var lvTenants: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenants)

        owner = intent.getStringExtra("owner") ?: ""
        dbHelper = RoomDbHelper(this)
        lvTenants = findViewById(R.id.lvTenants)

        findViewById<Button>(R.id.btnAddTenant).setOnClickListener {
            showTenantDialog(0L)
        }

        loadTenants()
    }

    private fun loadTenants() {
        val cursor = dbHelper.getTenants(owner)
        val list = ArrayList<String>()
        val ids = ArrayList<Long>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_TENANT_NAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_TENANT_PHONE))
            val idCard = cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COL_TENANT_IDCARD))

            list.add("Name: $name | Phone: $phone | ID Card: $idCard")
            ids.add(id)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvTenants.adapter = adapter

        lvTenants.setOnItemClickListener { _, _, position, _ ->
            val tenantId = ids[position]
            AlertDialog.Builder(this)
                .setTitle("Tenant Options")
                .setItems(arrayOf("Delete")) { _, _ ->
                    dbHelper.deleteTenant(tenantId)
                    Toast.makeText(this, "Tenant deleted", Toast.LENGTH_SHORT).show()
                    loadTenants()
                }
                .show()
        }
    }

    private fun showTenantDialog(tenantId: Long) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val etName = EditText(this).apply { hint = "Full Name" }
        val etPhone = EditText(this).apply { hint = "Phone Number"; inputType = android.text.InputType.TYPE_CLASS_PHONE }
        val etIdCard = EditText(this).apply { hint = "ID Card / Passport" }
        val etGender = EditText(this).apply { hint = "Gender" }
        val etDate = EditText(this).apply { hint = "Start Date (YYYY-MM-DD)" }

        layout.addView(etName)
        layout.addView(etPhone)
        layout.addView(etIdCard)
        layout.addView(etGender)
        layout.addView(etDate)

        AlertDialog.Builder(this)
            .setTitle("Add Tenant")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val idCard = etIdCard.text.toString().trim()
                val gender = etGender.text.toString().trim()
                val date = etDate.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dbHelper.addTenant(owner, 1L, name, phone, idCard, gender, date)
                Toast.makeText(this, "Tenant added", Toast.LENGTH_SHORT).show()
                loadTenants()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

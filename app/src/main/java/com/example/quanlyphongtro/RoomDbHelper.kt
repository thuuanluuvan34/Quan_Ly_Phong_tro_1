package com.example.quanlyphongtro

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RoomDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "room_management.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ROOMS = "rooms"
        const val TABLE_TENANTS = "tenants"
        const val TABLE_ELECTRIC = "electric_records"
        const val TABLE_BILLS = "bills"
        const val TABLE_PAYMENTS = "bill_payments"
        const val TABLE_CONTRACTS = "contracts"

        const val COL_ID = "id"
        const val COL_OWNER = "owner"

        const val COL_ROOM_NUMBER = "room_number"
        const val COL_ROOM_PRICE = "price"
        const val COL_ROOM_AREA = "area"
        const val COL_ROOM_STATUS = "status"
        const val COL_ROOM_DESC = "description"

        const val COL_TENANT_ROOM_ID = "room_id"
        const val COL_TENANT_NAME = "full_name"
        const val COL_TENANT_PHONE = "phone"
        const val COL_TENANT_IDCARD = "id_card"
        const val COL_TENANT_GENDER = "gender"
        const val COL_TENANT_START_DATE = "start_date"

        const val COL_ELEC_ROOM_ID = "room_id"
        const val COL_ELEC_MONTH = "month"
        const val COL_ELEC_OLD = "old_index"
        const val COL_ELEC_NEW = "new_index"
        const val COL_ELEC_CONSUMPTION = "consumption"
        const val COL_ELEC_PRICE = "price_per_kwh"
        const val COL_ELEC_TOTAL = "total_cost"

        const val COL_BILL_ROOM_ID = "room_id"
        const val COL_BILL_MONTH = "month"
        const val COL_BILL_RENT = "room_rent"
        const val COL_BILL_ELECTRIC = "electric_cost"
        const val COL_BILL_WATER = "water_cost"
        const val COL_BILL_OTHER = "other_cost"
        const val COL_BILL_TOTAL = "total_amount"
        const val COL_BILL_STATUS = "status"

        const val COL_PAY_BILL_ID = "bill_id"
        const val COL_PAY_DATE = "payment_date"
        const val COL_PAY_AMOUNT = "amount_paid"
        const val COL_PAY_METHOD = "payment_method"
        const val COL_PAY_NOTES = "notes"

        const val COL_CONTRACT_ROOM_ID = "room_id"
        const val COL_CONTRACT_TENANT_ID = "tenant_id"
        const val COL_CONTRACT_NUMBER = "contract_number"
        const val COL_CONTRACT_START = "start_date"
        const val COL_CONTRACT_END = "end_date"
        const val COL_CONTRACT_DEPOSIT = "deposit"
        const val COL_CONTRACT_TERMS = "terms"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_ROOMS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OWNER TEXT,
                $COL_ROOM_NUMBER TEXT,
                $COL_ROOM_PRICE REAL,
                $COL_ROOM_AREA REAL,
                $COL_ROOM_STATUS TEXT,
                $COL_ROOM_DESC TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_TENANTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OWNER TEXT,
                $COL_TENANT_ROOM_ID INTEGER,
                $COL_TENANT_NAME TEXT,
                $COL_TENANT_PHONE TEXT,
                $COL_TENANT_IDCARD TEXT,
                $COL_TENANT_GENDER TEXT,
                $COL_TENANT_START_DATE TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_ELECTRIC (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OWNER TEXT,
                $COL_ELEC_ROOM_ID INTEGER,
                $COL_ELEC_MONTH TEXT,
                $COL_ELEC_OLD INTEGER,
                $COL_ELEC_NEW INTEGER,
                $COL_ELEC_CONSUMPTION INTEGER,
                $COL_ELEC_PRICE REAL,
                $COL_ELEC_TOTAL REAL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_BILLS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OWNER TEXT,
                $COL_BILL_ROOM_ID INTEGER,
                $COL_BILL_MONTH TEXT,
                $COL_BILL_RENT REAL,
                $COL_BILL_ELECTRIC REAL,
                $COL_BILL_WATER REAL,
                $COL_BILL_OTHER REAL,
                $COL_BILL_TOTAL REAL,
                $COL_BILL_STATUS TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_PAYMENTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PAY_BILL_ID INTEGER,
                $COL_PAY_DATE TEXT,
                $COL_PAY_AMOUNT REAL,
                $COL_PAY_METHOD TEXT,
                $COL_PAY_NOTES TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_CONTRACTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OWNER TEXT,
                $COL_CONTRACT_ROOM_ID INTEGER,
                $COL_CONTRACT_TENANT_ID INTEGER,
                $COL_CONTRACT_NUMBER TEXT,
                $COL_CONTRACT_START TEXT,
                $COL_CONTRACT_END TEXT,
                $COL_CONTRACT_DEPOSIT REAL,
                $COL_CONTRACT_TERMS TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTRACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PAYMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BILLS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ELECTRIC")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TENANTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROOMS")
        onCreate(db)
    }

    fun addRoom(owner: String, number: String, price: Double, area: Double, status: String, desc: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_OWNER, owner)
            put(COL_ROOM_NUMBER, number)
            put(COL_ROOM_PRICE, price)
            put(COL_ROOM_AREA, area)
            put(COL_ROOM_STATUS, status)
            put(COL_ROOM_DESC, desc)
        }
        val id = db.insert(TABLE_ROOMS, null, values)
        db.close()
        return id != -1L
    }

    fun getRooms(owner: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_ROOMS WHERE $COL_OWNER = ?", arrayOf(owner))
    }

    fun updateRoom(id: Long, number: String, price: Double, area: Double, status: String, desc: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ROOM_NUMBER, number)
            put(COL_ROOM_PRICE, price)
            put(COL_ROOM_AREA, area)
            put(COL_ROOM_STATUS, status)
            put(COL_ROOM_DESC, desc)
        }
        val rows = db.update(TABLE_ROOMS, values, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun deleteRoom(id: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_ROOMS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun addTenant(owner: String, roomId: Long, name: String, phone: String, idCard: String, gender: String, startDate: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_OWNER, owner)
            put(COL_TENANT_ROOM_ID, roomId)
            put(COL_TENANT_NAME, name)
            put(COL_TENANT_PHONE, phone)
            put(COL_TENANT_IDCARD, idCard)
            put(COL_TENANT_GENDER, gender)
            put(COL_TENANT_START_DATE, startDate)
        }
        val id = db.insert(TABLE_TENANTS, null, values)
        db.close()
        return id != -1L
    }

    fun getTenants(owner: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_TENANTS WHERE $COL_OWNER = ?", arrayOf(owner))
    }

    fun updateTenant(id: Long, roomId: Long, name: String, phone: String, idCard: String, gender: String, startDate: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TENANT_ROOM_ID, roomId)
            put(COL_TENANT_NAME, name)
            put(COL_TENANT_PHONE, phone)
            put(COL_TENANT_IDCARD, idCard)
            put(COL_TENANT_GENDER, gender)
            put(COL_TENANT_START_DATE, startDate)
        }
        val rows = db.update(TABLE_TENANTS, values, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun deleteTenant(id: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_TENANTS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun addElectricRecord(owner: String, roomId: Long, month: String, oldIdx: Int, newIdx: Int, pricePerKwh: Double): Boolean {
        val consumption = if (newIdx >= oldIdx) newIdx - oldIdx else 0
        val totalCost = consumption * pricePerKwh
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_OWNER, owner)
            put(COL_ELEC_ROOM_ID, roomId)
            put(COL_ELEC_MONTH, month)
            put(COL_ELEC_OLD, oldIdx)
            put(COL_ELEC_NEW, newIdx)
            put(COL_ELEC_CONSUMPTION, consumption)
            put(COL_ELEC_PRICE, pricePerKwh)
            put(COL_ELEC_TOTAL, totalCost)
        }
        val id = db.insert(TABLE_ELECTRIC, null, values)
        db.close()
        return id != -1L
    }

    fun getElectricRecords(owner: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_ELECTRIC WHERE $COL_OWNER = ?", arrayOf(owner))
    }

    fun deleteElectricRecord(id: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_ELECTRIC, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun addBill(owner: String, roomId: Long, month: String, rent: Double, electric: Double, water: Double, other: Double, status: String): Boolean {
        val total = rent + electric + water + other
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_OWNER, owner)
            put(COL_BILL_ROOM_ID, roomId)
            put(COL_BILL_MONTH, month)
            put(COL_BILL_RENT, rent)
            put(COL_BILL_ELECTRIC, electric)
            put(COL_BILL_WATER, water)
            put(COL_BILL_OTHER, other)
            put(COL_BILL_TOTAL, total)
            put(COL_BILL_STATUS, status)
        }
        val id = db.insert(TABLE_BILLS, null, values)
        db.close()
        return id != -1L
    }

    fun getBills(owner: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_BILLS WHERE $COL_OWNER = ?", arrayOf(owner))
    }

    fun updateBillStatus(id: Long, status: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_BILL_STATUS, status)
        }
        val rows = db.update(TABLE_BILLS, values, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun deleteBill(id: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_BILLS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun addPayment(billId: Long, date: String, amount: Double, method: String, notes: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PAY_BILL_ID, billId)
            put(COL_PAY_DATE, date)
            put(COL_PAY_AMOUNT, amount)
            put(COL_PAY_METHOD, method)
            put(COL_PAY_NOTES, notes)
        }
        val id = db.insert(TABLE_PAYMENTS, null, values)
        if (id != -1L) {
            updateBillStatus(billId, "Paid")
        }
        db.close()
        return id != -1L
    }

    fun getPayments(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_PAYMENTS", null)
    }

    fun addContract(owner: String, roomId: Long, tenantId: Long, contractNumber: String, startDate: String, endDate: String, deposit: Double, terms: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_OWNER, owner)
            put(COL_CONTRACT_ROOM_ID, roomId)
            put(COL_CONTRACT_TENANT_ID, tenantId)
            put(COL_CONTRACT_NUMBER, contractNumber)
            put(COL_CONTRACT_START, startDate)
            put(COL_CONTRACT_END, endDate)
            put(COL_CONTRACT_DEPOSIT, deposit)
            put(COL_CONTRACT_TERMS, terms)
        }
        val id = db.insert(TABLE_CONTRACTS, null, values)
        db.close()
        return id != -1L
    }

    fun getContracts(owner: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_CONTRACTS WHERE $COL_OWNER = ?", arrayOf(owner))
    }

    fun deleteContract(id: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_CONTRACTS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun getTotalRevenue(owner: String): Double {
        val db = readableDatabase
        var total = 0.0
        val cursor = db.rawQuery("SELECT SUM($COL_BILL_TOTAL) FROM $TABLE_BILLS WHERE $COL_OWNER = ? AND $COL_BILL_STATUS = 'Paid'", arrayOf(owner))
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun getRoomCounts(owner: String): Pair<Int, Int> {
        val db = readableDatabase
        var total = 0
        var occupied = 0
        val cursor = db.rawQuery("SELECT $COL_ROOM_STATUS FROM $TABLE_ROOMS WHERE $COL_OWNER = ?", arrayOf(owner))
        while (cursor.moveToNext()) {
            total++
            if (cursor.getString(0) == "Occupied") {
                occupied++
            }
        }
        cursor.close()
        db.close()
        return Pair(total, occupied)
    }
}

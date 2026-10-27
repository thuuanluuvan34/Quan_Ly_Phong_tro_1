package com.example.quanlyphongtro

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_management.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD_HASH = "password_hash"
        const val COLUMN_SALT = "salt"
        const val COLUMN_ROLE = "role" // "ADMIN" or "OWNER"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_STATUS = "status" // "ACTIVE" or "LOCKED"

        private const val ITERATIONS = 250000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH = 32

        fun hashPassword(password: String, salt: ByteArray): String {
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded
            return Base64.encodeToString(hash, Base64.NO_WRAP)
        }

        fun generateSalt(): ByteArray {
            val sr = SecureRandom()
            val salt = ByteArray(SALT_LENGTH)
            sr.nextBytes(salt)
            return salt
        }

        fun verifyPassword(password: String, storedHash: String, storedSaltBase64: String): Boolean {
            val salt = Base64.decode(storedSaltBase64, Base64.NO_WRAP)
            val computedHash = hashPassword(password, salt)
            return computedHash == storedHash
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT UNIQUE, " +
                "$COLUMN_PASSWORD_HASH TEXT, " +
                "$COLUMN_SALT TEXT, " +
                "$COLUMN_ROLE TEXT, " +
                "$COLUMN_FULL_NAME TEXT, " +
                "$COLUMN_PHONE TEXT, " +
                "$COLUMN_STATUS TEXT)")
        db.execSQL(createTableQuery)

        seedDefaultAdmin(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun seedDefaultAdmin(db: SQLiteDatabase) {
        val username = "admin"
        val password = "admin123"
        val saltBytes = generateSalt()
        val saltBase64 = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val hash = hashPassword(password, saltBytes)

        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD_HASH, hash)
            put(COLUMN_SALT, saltBase64)
            put(COLUMN_ROLE, "ADMIN")
            put(COLUMN_FULL_NAME, "System Administrator")
            put(COLUMN_PHONE, "0123456789")
            put(COLUMN_STATUS, "ACTIVE")
        }
        db.insert(TABLE_USERS, null, values)
    }

    fun registerUser(username: String, password: String, role: String, fullName: String, phone: String): Boolean {
        val db = writableDatabase
        try {
            val saltBytes = generateSalt()
            val saltBase64 = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
            val hash = hashPassword(password, saltBytes)

            val values = ContentValues().apply {
                put(COLUMN_USERNAME, username)
                put(COLUMN_PASSWORD_HASH, hash)
                put(COLUMN_SALT, saltBase64)
                put(COLUMN_ROLE, role)
                put(COLUMN_FULL_NAME, fullName)
                put(COLUMN_PHONE, phone)
                put(COLUMN_STATUS, "ACTIVE")
            }
            val result = db.insert(TABLE_USERS, null, values)
            return result != -1L
        } catch (e: Exception) {
            return false
        } finally {
            db.close()
        }
    }

    fun authenticateUser(username: String, password: String): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_STATUS = 'ACTIVE'", arrayOf(username))
        if (cursor != null && cursor.moveToFirst()) {
            val storedHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH))
            val storedSalt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALT))
            if (verifyPassword(password, storedHash, storedSalt)) {
                return cursor
            }
            cursor.close()
        }
        return null
    }

    fun getUserRole(username: String): String? {
        val db = readableDatabase
        var role: String? = null
        val cursor = db.rawQuery("SELECT $COLUMN_ROLE FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))
        if (cursor.moveToFirst()) {
            role = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return role
    }

    fun updatePassword(username: String, newPassword: String): Boolean {
        val db = writableDatabase
        val saltBytes = generateSalt()
        val saltBase64 = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val hash = hashPassword(newPassword, saltBytes)

        val values = ContentValues().apply {
            put(COLUMN_PASSWORD_HASH, hash)
            put(COLUMN_SALT, saltBase64)
        }
        val rows = db.update(TABLE_USERS, values, "$COLUMN_USERNAME = ?", arrayOf(username))
        db.close()
        return rows > 0
    }

    fun getAllUsers(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USERS", null)
    }

    fun updateUserStatus(userId: Long, status: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, status)
        }
        val rows = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
        return rows > 0
    }

    fun deleteUser(userId: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
        return rows > 0
    }
}

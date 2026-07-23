package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "UsersDB"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "users"

        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_LASTNAME = "lastname"
        const val COL_AGE = "age"
        const val COL_GENDER = "gender"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = """
            CREATE TABLE $TABLE_NAME(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT,
                $COL_LASTNAME TEXT,
                $COL_AGE TEXT,
                $COL_GENDER TEXT
            )
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(
        name: String,
        lastname: String,
        age: String,
        gender: String
    ): Boolean {

        val db = this.writableDatabase

        val values = ContentValues()

        values.put(COL_NAME, name)
        values.put(COL_LASTNAME, lastname)
        values.put(COL_AGE, age)
        values.put(COL_GENDER, gender)

        val result = db.insert(TABLE_NAME, null, values)

        db.close()

        return result != -1L
    }

    fun getAllUsers(): List<Map<String, String>> {

        val usersList = mutableListOf<Map<String, String>>()

        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME",
            null
        )

        if (cursor.moveToFirst()) {

            do {

                val user = mapOf(

                    "id" to cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID)
                    ).toString(),

                    "name" to cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_NAME)
                    ),

                    "lastname" to cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_LASTNAME)
                    ),

                    "age" to cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_AGE)
                    ),

                    "gender" to cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_GENDER)
                    )
                )

                usersList.add(user)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return usersList
    }
    fun deleteUser(id: Int): Boolean {

        val db = this.writableDatabase

        val result = db.delete(
            TABLE_NAME,
            "$COL_ID=?",
            arrayOf(id.toString())
        )

        db.close()

        return result > 0
    }

    fun updateUser(
        id: Int,
        name: String,
        lastname: String,
        age: String,
        gender: String
    ): Boolean {

        val db = this.writableDatabase

        val values = ContentValues()

        values.put(COL_NAME, name)
        values.put(COL_LASTNAME, lastname)
        values.put(COL_AGE, age)
        values.put(COL_GENDER, gender)

        val result = db.update(
            TABLE_NAME,
            values,
            "$COL_ID=?",
            arrayOf(id.toString())
        )

        db.close()

        return result > 0
    }
}
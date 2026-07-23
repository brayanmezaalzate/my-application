package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "tasks.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE tasks(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT,
                priority TEXT,
                dueDate TEXT,
                completed INTEGER
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    fun insertTask(title: String, description: String, priority: String, dueDate: String): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put("title", title)
        values.put("description", description)
        values.put("priority", priority)
        values.put("dueDate", dueDate)
        values.put("completed", 0)

        return db.insert("tasks", null, values) > 0
    }

    fun getTasks(): List<Map<String, String>> {

        val list = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tasks", null)

        while (cursor.moveToNext()) {

            val task = mapOf(
                "id" to cursor.getInt(0).toString(),
                "title" to cursor.getString(1),
                "description" to cursor.getString(2),
                "priority" to cursor.getString(3),
                "dueDate" to cursor.getString(4),
                "completed" to cursor.getInt(5).toString()
            )

            list.add(task)
        }

        cursor.close()
        return list
    }

    fun updateTask(
        id: Int,
        title: String,
        description: String,
        priority: String,
        dueDate: String,
        completed: Int
    ): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put("title", title)
        values.put("description", description)
        values.put("priority", priority)
        values.put("dueDate", dueDate)
        values.put("completed", completed)

        return db.update("tasks", values, "id=?", arrayOf(id.toString())) > 0
    }

    fun deleteTask(id: Int): Boolean {

        val db = writableDatabase
        return db.delete("tasks", "id=?", arrayOf(id.toString())) > 0
    }
}
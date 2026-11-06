package com.example.lab15_16

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, "DBName", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS MyTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name VARCHAR)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS MyTable")
        onCreate(db)
    }

    // Добавление записи
    fun addRecord(name: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply { put("Name", name) }
        db.insert("MyTable", null, values)
        db.close()
    }

    // Получение всех записей
    fun getAllRecords(): List<String> {
        val list = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM MyTable", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndexOrThrow("Name")))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
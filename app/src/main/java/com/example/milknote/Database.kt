package com.example.milknote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getDoubleOrNull


class Database(context: Context) : SQLiteOpenHelper(context,  DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "customersData"

        private const val TABLE_NAMES = "names"

        private const val TABLE_WEEKS = "weeks"

        private const val TABLE_ID = "id"
        private const val TABLE_NAME = "name"

        private const val TABLE_WEEK = "week"
        private const val TABLE_WEEK_MEAL = "meal"

        private const val WEEK_LENGTH = 14
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createNames =  "CREATE TABLE IF NOT EXISTS $TABLE_NAMES( $TABLE_ID INTEGER PRIMARY KEY, $TABLE_NAME TEXT)"
        db?.execSQL(createNames)
        val createWeeks =  "CREATE TABLE IF NOT EXISTS $TABLE_WEEKS( $TABLE_ID INTEGER PRIMARY KEY, $TABLE_NAME TEXT)"
        db?.execSQL(createWeeks)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    // Names

    fun getNames() : Array<String> {
        val names = ArrayList<String>()

        val selectQuery = "SELECT  * FROM $TABLE_NAMES"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1))
            } while (cursor.moveToNext())
        }
        cursor.close()

        return names.toTypedArray()
    }

    fun addName(name: String){
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(TABLE_NAME, name)

        db.insert(TABLE_NAMES, null, values)

        db.close()
    }

    fun deleteName(name: String) {
        val db = this.writableDatabase
        db.delete(
            TABLE_NAMES,
            "$TABLE_NAME = ?",
            arrayOf(name)
        )
        db.close()
    }

    // Weeks
    fun getWeeks() : Array<String> {
        val names = ArrayList<String>()

        val selectQuery = "SELECT  * FROM $TABLE_WEEKS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1))
            } while (cursor.moveToNext())
        }
        cursor.close()

        return names.toTypedArray()
    }

    fun createWeek(week: String) {

        val db = this.writableDatabase

        if (weekExists(week))
            db.execSQL("DROP TABLE $TABLE_WEEK$week")
        else
        {
            val values = ContentValues()
            values.put(TABLE_NAME, week)
            db.insert(TABLE_WEEKS, null, values)
        }

        val entities = Array(WEEK_LENGTH) { "$TABLE_WEEK_MEAL$it" } .joinToString(" , ")
        val create =  "CREATE TABLE IF NOT EXISTS $TABLE_WEEK$week( $TABLE_ID INTEGER PRIMARY KEY, $TABLE_NAME TEXT, $entities)"
        db?.execSQL(create)

        for (name in getNames())
        {
            val values = ContentValues()

            values.put(TABLE_NAME, name)

            db.insert("$TABLE_WEEK$week", null, values)
        }
    }

    fun getWeek(week: String) : Array<Customer> {
        val customers = ArrayList<Customer>()

        val selectQuery = "SELECT  * FROM $TABLE_WEEK$week"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val customer = Customer(week, cursor.getString(1), arrayOfNulls<Double?>(WEEK_LENGTH))
                for (i in 0 until WEEK_LENGTH)
                    customer.values[i] = cursor.getDoubleOrNull(2 + i)
                customers.add(customer)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return customers.toTypedArray()
    }

    private fun weekExists(week: String) : Boolean = getWeeks().contains(week)

    // Customers
    fun addCustomer(week: String, name: String) : Customer{
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(TABLE_NAME, name)

        db.insert(TABLE_NAMES, null, values)

        db.insert("$TABLE_WEEK$week", null, values)

        db.close()

        return Customer(week, name, arrayOfNulls<Double?>(WEEK_LENGTH))
    }

    fun setValue(week: String,name: String, i: Int, value: Double?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("$TABLE_WEEK_MEAL$i", value)

        db.update(
            "$TABLE_WEEK$week",
            values,
            "$TABLE_NAME = ?",
            arrayOf(name)
        )
    }
}
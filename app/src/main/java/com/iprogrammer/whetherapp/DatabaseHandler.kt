package com.iprogrammer.whetherapp

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException


class DatabaseHandler(context: MainActivity): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "CitiesDatabase"
        private val TABLE_CONTACTS = "CitiesTable"
        private val KEY_CITYNAME = "cityname"
        private val KEY_CITYTEMP = "temp"
        private val KEY_DATETIME = "datetime"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_CITYNAME + " TEXT," + KEY_CITYTEMP + " TEXT,"
                + KEY_DATETIME + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addCities(city: Cities):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CITYNAME, city.cityName)
        contentValues.put(KEY_CITYTEMP,city.cityTemp )
        contentValues.put(KEY_DATETIME,city.cityDate)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }
    //method to read data
    fun viewCities():List<Cities>{
        val cityList:ArrayList<Cities> = ArrayList<Cities>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var cityName: String
        var cityTemp: String
        var dateTime: String
        if (cursor.moveToFirst()) {
            do {
                cityName = cursor.getString(cursor.getColumnIndex("cityname"))
                cityTemp = cursor.getString(cursor.getColumnIndex("temp"))
                dateTime = cursor.getString(cursor.getColumnIndex("datetime"))

                val city= Cities(cityName = cityName, cityTemp = cityTemp , cityDate = dateTime)
                cityList.add(city)
            } while (cursor.moveToNext())
        }
        return cityList
    }

    //method to delete data
  /*  fun deleteEmployee(emp: Cities):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"id="+emp.userId,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }*/
}
package com.shiro.notesapp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.media.projection.MediaProjection

class NoteDB {
    var dbName = "NoteDB"
    var dbTable = "NoteTable"

    var colID = "ID"
    var colDesc = "Description"
    var colDateTime = "Datetime"
    var colTagList = "Taglist"

    var dbVersion = 1

    var createTable = "CREATE TABLE IF NOT EXISTS "+dbTable+"("+colID+" INTEGER PRIMARY KEY," + colDesc + " TEXT,"+ colTagList+" TEXT," + colDateTime+" TEXT);"

    var sqlDB: SQLiteDatabase ?= null;

    constructor(context: Context) {
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }
    inner class DatabaseHelperNotes:SQLiteOpenHelper {
        var context:Context ?= null
        constructor(context: Context):super(context,dbName,null,dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(createTable)

        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS " + dbName)
        }
    }

    fun insert(value:ContentValues) : Long {

        var ID = sqlDB!!.insert(dbTable,"",value)
        return ID
    }

    fun query(projection:Array<String>, selection:String, selectionArgs:Array<String>,sortOrder: String):Cursor {
        var qb = SQLiteQueryBuilder()
        qb.tables = dbTable
        return qb.query(sqlDB,projection,selection,selectionArgs,null,null,sortOrder)
    }

    fun delete(selection:String, selectionArgs:Array<String>) {
        sqlDB!!.delete(dbTable,selection,selectionArgs)
    }

    fun update(value:ContentValues,selection:String, selectionArgs:Array<String> ):Int {
        val count = sqlDB!!.update(dbTable,value,selection,selectionArgs)
        return count
    }
}
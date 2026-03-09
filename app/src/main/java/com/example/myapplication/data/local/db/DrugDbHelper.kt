package com.example.myapplication.data.local.db

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.database.sqlite.SQLiteDatabase

class DrugDbHelper(private val context: Context) {

    private val dbName = "drugdb_acne_enriched.sqlite3"
    private val dbPath: String
        get() = context.getDatabasePath(dbName).path

    fun openDatabase(): SQLiteDatabase {
        copyDatabaseIfNeeded()
        return SQLiteDatabase.openDatabase(
            dbPath,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }

    private fun copyDatabaseIfNeeded() {
        val dbFile = File(dbPath)
        if (dbFile.exists()) return

        dbFile.parentFile?.mkdirs()

        context.assets.open(dbName).use { input ->
            FileOutputStream(dbFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}

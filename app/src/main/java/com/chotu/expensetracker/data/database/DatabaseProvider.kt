package com.chotu.expensetracker.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    fun getDatabase(context: Context): ExpenseDatabase {
        return Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "expense_database"
        ).build()
    }
}
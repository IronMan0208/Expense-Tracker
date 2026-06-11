package com.chotu.expensetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chotu.expensetracker.data.dao.ExpenseDao
import com.chotu.expensetracker.data.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
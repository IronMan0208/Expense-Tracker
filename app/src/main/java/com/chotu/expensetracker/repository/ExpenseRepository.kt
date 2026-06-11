package com.chotu.expensetracker.repository

import com.chotu.expensetracker.data.dao.ExpenseDao
import com.chotu.expensetracker.data.entity.ExpenseEntity

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {
    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun getAllExpenses(): List<ExpenseEntity> {
        return expenseDao.getAllExpenses()
    }

}
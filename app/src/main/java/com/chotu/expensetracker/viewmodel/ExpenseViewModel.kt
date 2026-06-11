package com.chotu.expensetracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chotu.expensetracker.data.entity.ExpenseEntity
import com.chotu.expensetracker.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    var expenses by mutableStateOf<List<ExpenseEntity>>(emptyList())

    fun loadExpenses() {
        viewModelScope.launch {
            expenses = repository.getAllExpenses()
        }
    }

    fun insertExpense(
        title: String,
        amount: Double
    ) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount
                )
            )
            loadExpenses()
        }

    }

    fun deleteExpense(
        expense: ExpenseEntity
    ) {
        viewModelScope.launch {
            repository.deleteExpense(
                expense
            )
            loadExpenses()
        }
    }
}
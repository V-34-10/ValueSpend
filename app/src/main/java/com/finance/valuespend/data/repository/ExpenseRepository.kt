package com.finance.valuespend.data.repository

import com.finance.valuespend.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class ExpenseFilter(
    val category: String? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val minSatisfaction: Int? = null,
    val maxSatisfaction: Int? = null
)

interface ExpenseRepository {
    fun observeExpenses(filter: ExpenseFilter): Flow<List<Expense>>
    fun observeExpense(id: Long): Flow<Expense?>
    suspend fun getExpense(id: Long): Expense?
    suspend fun getExpenses(filter: ExpenseFilter): List<Expense>
    suspend fun addExpense(
        amount: Double,
        category: String,
        date: LocalDate,
        satisfaction: Int,
        comment: String?
    ): Long

    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
    suspend fun deleteAllExpenses()
}


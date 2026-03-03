package com.finance.valuespend.data.repository

import com.finance.valuespend.data.db.ExpenseDao
import com.finance.valuespend.data.db.ExpenseEntity
import com.finance.valuespend.data.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomExpenseRepository(
    private val dao: ExpenseDao
) : ExpenseRepository {
    override fun observeExpenses(filter: ExpenseFilter): Flow<List<Expense>> {
        return dao.observeFiltered(
            category = filter.category,
            fromEpochDay = filter.from?.toEpochDay(),
            toEpochDay = filter.to?.toEpochDay(),
            minSatisfaction = filter.minSatisfaction,
            maxSatisfaction = filter.maxSatisfaction
        ).map { list -> list.map { it.toModel() } }
    }

    override fun observeExpense(id: Long): Flow<Expense?> {
        return dao.observeById(id).map { it?.toModel() }
    }

    override suspend fun getExpense(id: Long): Expense? = dao.getById(id)?.toModel()

    override suspend fun getExpenses(filter: ExpenseFilter): List<Expense> {
        return dao.getFiltered(
            category = filter.category,
            fromEpochDay = filter.from?.toEpochDay(),
            toEpochDay = filter.to?.toEpochDay(),
            minSatisfaction = filter.minSatisfaction,
            maxSatisfaction = filter.maxSatisfaction
        ).map { it.toModel() }
    }

    override suspend fun addExpense(
        amount: Double,
        category: String,
        date: LocalDate,
        satisfaction: Int,
        comment: String?
    ): Long {
        val entity = ExpenseEntity(
            amount = amount,
            category = category,
            dateEpochDay = date.toEpochDay(),
            satisfaction = satisfaction,
            comment = comment,
            createdAtEpochMs = System.currentTimeMillis()
        )
        return dao.insert(entity)
    }

    override suspend fun updateExpense(expense: Expense) {
        dao.update(expense.toEntity())
    }

    override suspend fun deleteExpense(id: Long) {
        val entity = dao.getById(id) ?: return
        dao.delete(entity)
    }

    override suspend fun deleteAllExpenses() {
        dao.deleteAll()
    }
}

private fun ExpenseEntity.toModel(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        date = LocalDate.ofEpochDay(dateEpochDay),
        satisfaction = satisfaction,
        comment = comment,
        createdAtEpochMs = createdAtEpochMs
    )
}

private fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        dateEpochDay = date.toEpochDay(),
        satisfaction = satisfaction,
        comment = comment,
        createdAtEpochMs = createdAtEpochMs
    )
}


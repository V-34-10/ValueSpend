package com.finance.valuespend.ui.state

import com.finance.valuespend.data.model.Expense

data class ExpenseListUiState(
    val expenses: List<Expense> = emptyList()
)


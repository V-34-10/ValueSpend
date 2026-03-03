package com.finance.valuespend.ui.state

import java.time.LocalDate

data class EditExpenseUiState(
    val expenseId: Long = 0,
    val createdAtEpochMs: Long = 0,
    val amountText: String = "",
    val category: String? = null,
    val date: LocalDate = LocalDate.now(),
    val satisfaction: Int = 3,
    val commentText: String = "",
    val amountError: FieldError? = null,
    val categoryError: FieldError? = null,
    val dateError: FieldError? = null,
    val satisfactionError: FieldError? = null,
    val commentError: FieldError? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
)


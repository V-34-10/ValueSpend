package com.finance.valuespend.ui.state

import java.time.LocalDate

data class AddExpenseUiState(
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
    val isSaving: Boolean = false
)

enum class FieldError {
    REQUIRED,
    INVALID,
    OUT_OF_RANGE,
    TOO_LONG
}


package com.finance.valuespend.data.model

import java.time.LocalDate

data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val date: LocalDate,
    val satisfaction: Int,
    val comment: String?,
    val createdAtEpochMs: Long
)


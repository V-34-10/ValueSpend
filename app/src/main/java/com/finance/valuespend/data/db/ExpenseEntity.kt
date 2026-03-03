package com.finance.valuespend.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["dateEpochDay"]),
        Index(value = ["category"]),
        Index(value = ["satisfaction"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    /**
     * Stored as epoch day (UTC) to keep Room schema simple and stable.
     */
    val dateEpochDay: Long,
    /**
     * 1..5
     */
    val satisfaction: Int,
    val comment: String?,
    val createdAtEpochMs: Long
)


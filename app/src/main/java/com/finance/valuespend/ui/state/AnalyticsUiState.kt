package com.finance.valuespend.ui.state

data class CategorySum(
    val categoryId: String,
    val totalAmount: Double,
    val count: Int
)

data class AnalyticsUiState(
    val categorySums: List<CategorySum> = emptyList(),
    val averageSatisfaction: Double = 0.0,
    val totalAmount: Double = 0.0,
    val expenseCount: Int = 0,
    val satisfactionByCategory: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = true
)

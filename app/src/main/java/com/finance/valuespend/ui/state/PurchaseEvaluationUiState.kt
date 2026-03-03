package com.finance.valuespend.ui.state

data class PurchaseEvaluationUiState(
    val plannedAmountText: String = "",
    val expectedFrequencyText: String = "",
    val expectedUtility: Int = 3,
    val plannedAmountError: FieldError? = null,
    val expectedFrequencyError: FieldError? = null,
    val result: EvaluationResult? = null
)

data class EvaluationResult(
    val costPerUse: Double,
    val valueIndex: Double,
    val comparisonText: Int,
    val medianHistoryValueIndex: Double?
)

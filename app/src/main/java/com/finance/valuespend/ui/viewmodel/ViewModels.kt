package com.finance.valuespend.ui.viewmodel

import com.finance.valuespend.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.finance.valuespend.data.datastore.AppPreferences
import com.finance.valuespend.data.repository.ExpenseRepository
import com.finance.valuespend.data.repository.ExpenseFilter
import com.finance.valuespend.data.model.Expense
import com.finance.valuespend.ui.state.AddExpenseUiState
import com.finance.valuespend.ui.state.ExpenseDetailUiState
import com.finance.valuespend.ui.state.EditExpenseUiState
import com.finance.valuespend.ui.state.EvaluationResult
import com.finance.valuespend.ui.state.ExpenseListUiState
import com.finance.valuespend.ui.state.FieldError
import com.finance.valuespend.ui.state.HomeUiState
import com.finance.valuespend.ui.state.PurchaseEvaluationUiState
import com.finance.valuespend.ui.state.AnalyticsUiState
import com.finance.valuespend.ui.state.CategorySum
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class PreloaderViewModel(
    private val preferences: AppPreferences
) : ViewModel() {
    private val _destination = MutableSharedFlow<Boolean>(replay = 1)
    val destination: SharedFlow<Boolean> = _destination.asSharedFlow()

    init {
        viewModelScope.launch {
            val completed = preferences.onboardingCompleted.first()
            delay(900)
            _destination.emit(!completed)
        }
    }
}

class OnboardingViewModel(
    private val preferences: AppPreferences
) : ViewModel() {
    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted(true)
        }
    }
}

class HomeViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val ym = YearMonth.now()
            val from = ym.atDay(1)
            val to = ym.atEndOfMonth()
            repository.observeExpenses(
                ExpenseFilter(from = from, to = to)
            ).collectLatest { expenses ->
                val avg = if (expenses.isEmpty()) 0.0 else expenses.map { it.satisfaction }.average()
                _uiState.value = HomeUiState(
                    totalExpenses = expenses.size,
                    averageSatisfaction = avg
                )
            }
        }
    }
}

class AddExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun onAmountChanged(text: String) {
        _uiState.value = _uiState.value.copy(amountText = text, amountError = null)
    }

    fun onCategoryChanged(category: String) {
        _uiState.value = _uiState.value.copy(category = category, categoryError = null)
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date, dateError = null)
    }

    fun onSatisfactionChanged(value: Int) {
        _uiState.value = _uiState.value.copy(satisfaction = value, satisfactionError = null)
    }

    fun onCommentChanged(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text, commentError = null)
    }

    fun submit() {
        val current = _uiState.value
        val amount = current.amountText.replace(',', '.').toDoubleOrNull()
        val amountError = when {
            current.amountText.isBlank() -> FieldError.REQUIRED
            amount == null -> FieldError.INVALID
            amount <= 0.0 || amount >= 1_000_000.0 -> FieldError.OUT_OF_RANGE
            else -> null
        }

        val categoryError = if (current.category.isNullOrBlank()) FieldError.REQUIRED else null

        val dateError = if (current.date.isAfter(LocalDate.now())) FieldError.INVALID else null

        val satisfactionError = if (current.satisfaction !in 1..5) FieldError.OUT_OF_RANGE else null

        val commentError = when {
            current.commentText.length > 200 -> FieldError.TOO_LONG
            current.commentText.isNotBlank() && !current.commentText.matches(
                Regex("""^[\p{L}\p{N}\s\.,\-!\?]*$""")
            ) -> FieldError.INVALID
            else -> null
        }

        val hasErrors = listOf(
            amountError,
            categoryError,
            dateError,
            satisfactionError,
            commentError
        ).any { it != null }

        if (hasErrors) {
            _uiState.value = current.copy(
                amountError = amountError,
                categoryError = categoryError,
                dateError = dateError,
                satisfactionError = satisfactionError,
                commentError = commentError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isSaving = true)
            repository.addExpense(
                amount = amount!!,
                category = current.category!!,
                date = current.date,
                satisfaction = current.satisfaction,
                comment = current.commentText.trim().ifBlank { null }
            )
            _uiState.value = AddExpenseUiState()
            _events.tryEmit(Unit)
        }
    }
}

class ExpenseListViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeExpenses(ExpenseFilter()).collectLatest { expenses ->
                _uiState.value = ExpenseListUiState(expenses = expenses)
            }
        }
    }
}

class ExpenseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ExpenseRepository
) : ViewModel() {
    private val expenseId: Long = checkNotNull(savedStateHandle.get<Long>("expenseId"))

    val uiState: StateFlow<ExpenseDetailUiState> =
        repository.observeExpense(expenseId)
            .map { ExpenseDetailUiState(expense = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExpenseDetailUiState())

    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun delete() {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
            _events.tryEmit(Unit)
        }
    }
}

class EditExpenseViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ExpenseRepository
) : ViewModel() {
    private val expenseId: Long = checkNotNull(savedStateHandle.get<Long>("expenseId"))

    private val _uiState = MutableStateFlow(EditExpenseUiState(expenseId = expenseId))
    val uiState: StateFlow<EditExpenseUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val expense = repository.getExpense(expenseId)
            if (expense != null) {
                _uiState.value = EditExpenseUiState(
                    expenseId = expense.id,
                    createdAtEpochMs = expense.createdAtEpochMs,
                    amountText = expense.amount.toString(),
                    category = expense.category,
                    date = expense.date,
                    satisfaction = expense.satisfaction,
                    commentText = expense.comment.orEmpty(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onAmountChanged(text: String) {
        _uiState.value = _uiState.value.copy(amountText = text, amountError = null)
    }

    fun onCategoryChanged(category: String) {
        _uiState.value = _uiState.value.copy(category = category, categoryError = null)
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date, dateError = null)
    }

    fun onSatisfactionChanged(value: Int) {
        _uiState.value = _uiState.value.copy(satisfaction = value, satisfactionError = null)
    }

    fun onCommentChanged(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text, commentError = null)
    }

    fun submit() {
        val current = _uiState.value
        val amount = current.amountText.replace(',', '.').toDoubleOrNull()
        val amountError = when {
            current.amountText.isBlank() -> FieldError.REQUIRED
            amount == null -> FieldError.INVALID
            amount <= 0.0 || amount >= 1_000_000.0 -> FieldError.OUT_OF_RANGE
            else -> null
        }

        val categoryError = if (current.category.isNullOrBlank()) FieldError.REQUIRED else null
        val dateError = if (current.date.isAfter(LocalDate.now())) FieldError.INVALID else null
        val satisfactionError = if (current.satisfaction !in 1..5) FieldError.OUT_OF_RANGE else null
        val commentError = when {
            current.commentText.length > 200 -> FieldError.TOO_LONG
            current.commentText.isNotBlank() && !current.commentText.matches(
                Regex("""^[\p{L}\p{N}\s\.,\-!\?]*$""")
            ) -> FieldError.INVALID
            else -> null
        }

        val hasErrors = listOf(
            amountError,
            categoryError,
            dateError,
            satisfactionError,
            commentError
        ).any { it != null }

        if (hasErrors) {
            _uiState.value = current.copy(
                amountError = amountError,
                categoryError = categoryError,
                dateError = dateError,
                satisfactionError = satisfactionError,
                commentError = commentError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isSaving = true)
            repository.updateExpense(
                Expense(
                    id = current.expenseId,
                    amount = amount!!,
                    category = current.category!!,
                    date = current.date,
                    satisfaction = current.satisfaction,
                    comment = current.commentText.trim().ifBlank { null },
                    createdAtEpochMs = current.createdAtEpochMs
                )
            )
            _events.tryEmit(Unit)
        }
    }
}

class PurchaseEvaluationViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PurchaseEvaluationUiState())
    val uiState: StateFlow<PurchaseEvaluationUiState> = _uiState.asStateFlow()

    fun onPlannedAmountChanged(text: String) {
        _uiState.value = _uiState.value.copy(plannedAmountText = text, plannedAmountError = null, result = null)
    }

    fun onExpectedFrequencyChanged(text: String) {
        _uiState.value = _uiState.value.copy(expectedFrequencyText = text, expectedFrequencyError = null, result = null)
    }

    fun onExpectedUtilityChanged(value: Int) {
        _uiState.value = _uiState.value.copy(expectedUtility = value.coerceIn(1, 5), result = null)
    }

    fun calculate() {
        val current = _uiState.value
        val amount = current.plannedAmountText.replace(',', '.').toDoubleOrNull()
        val amountError = when {
            current.plannedAmountText.isBlank() -> FieldError.REQUIRED
            amount == null -> FieldError.INVALID
            amount <= 0.0 || amount >= 1_000_000.0 -> FieldError.OUT_OF_RANGE
            else -> null
        }
        val frequency = current.expectedFrequencyText.replace(',', '.').toDoubleOrNull()
        val frequencyError = when {
            current.expectedFrequencyText.isBlank() -> FieldError.REQUIRED
            frequency == null -> FieldError.INVALID
            frequency <= 0.0 || frequency >= 1_000_000.0 -> FieldError.OUT_OF_RANGE
            else -> null
        }
        if (amountError != null || frequencyError != null) {
            _uiState.value = current.copy(plannedAmountError = amountError, expectedFrequencyError = frequencyError)
            return
        }
        val amt = amount!!
        val freq = frequency!!
        val costPerUse = amt / freq
        val utility = current.expectedUtility.toDouble()
        val valueIndex = if (costPerUse > 0) utility / costPerUse else 0.0
        viewModelScope.launch {
            val past = repository.getExpenses(ExpenseFilter(minSatisfaction = 4))
            val historyValueIndices = past.map { it.satisfaction.toDouble() / it.amount }.filter { it.isFinite() }
            val median = if (historyValueIndices.isNotEmpty()) {
                historyValueIndices.sorted().let { s ->
                    val m = s.size / 2
                    if (s.size % 2 == 1) s[m] else (s[m - 1] + s[m]) / 2
                }
            } else null
            val comparisonText = when {
                median == null -> R.string.evaluate_comparison_no_data
                valueIndex >= median -> R.string.evaluate_comparison_above_avg
                else -> R.string.evaluate_comparison_below_avg
            }
            _uiState.value = current.copy(
                result = EvaluationResult(
                    costPerUse = costPerUse,
                    valueIndex = valueIndex,
                    comparisonText = comparisonText,
                    medianHistoryValueIndex = median
                )
            )
        }
    }
}

class AnalyticsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ym = YearMonth.now()
            val from = ym.atDay(1)
            val to = ym.atEndOfMonth()
            val expenses = repository.getExpenses(ExpenseFilter(from = from, to = to))
            val categorySums = expenses.groupBy { it.category }.map { (cat, list) ->
                CategorySum(categoryId = cat, totalAmount = list.sumOf { it.amount }, count = list.size)
            }.sortedByDescending { it.totalAmount }
            val totalAmount = expenses.sumOf { it.amount }
            val avgSatisfaction = if (expenses.isEmpty()) 0.0 else expenses.map { it.satisfaction }.average()
            val satisfactionByCategory = expenses.groupBy { it.category }.mapValues { (_, list) ->
                list.map { it.satisfaction }.average()
            }
            _uiState.value = AnalyticsUiState(
                categorySums = categorySums,
                averageSatisfaction = avgSatisfaction,
                totalAmount = totalAmount,
                expenseCount = expenses.size,
                satisfactionByCategory = satisfactionByCategory,
                isLoading = false
            )
        }
    }
}

class SettingsViewModel(
    private val repository: ExpenseRepository,
    private val preferences: AppPreferences
) : ViewModel() {
    private val _clearDone = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val clearDone: SharedFlow<Unit> = _clearDone.asSharedFlow()

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllExpenses()
            preferences.clearAll()
            _clearDone.tryEmit(Unit)
        }
    }
}


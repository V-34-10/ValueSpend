package com.finance.valuespend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finance.valuespend.BuildConfig
import com.google.android.play.core.review.ReviewManagerFactory
import com.finance.valuespend.R
import com.finance.valuespend.ui.state.EvaluationResult
import com.finance.valuespend.ui.state.FieldError
import com.finance.valuespend.ui.state.CategorySum
import com.finance.valuespend.ui.theme.LocalAppTheme
import com.finance.valuespend.ui.viewmodel.AddExpenseViewModel
import com.finance.valuespend.ui.viewmodel.AnalyticsViewModel
import com.finance.valuespend.ui.viewmodel.EditExpenseViewModel
import com.finance.valuespend.ui.viewmodel.ExpenseDetailViewModel
import com.finance.valuespend.ui.viewmodel.ExpenseListViewModel
import com.finance.valuespend.ui.viewmodel.HomeViewModel
import com.finance.valuespend.ui.viewmodel.OnboardingViewModel
import com.finance.valuespend.ui.viewmodel.PreloaderViewModel
import com.finance.valuespend.ui.viewmodel.PurchaseEvaluationViewModel
import com.finance.valuespend.ui.viewmodel.SettingsViewModel
import com.finance.valuespend.utils.DefaultCategories
import com.finance.valuespend.utils.toUiDate
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun PreloaderScreen(
    onFinished: (isFirstLaunch: Boolean) -> Unit,
    viewModel: PreloaderViewModel = koinViewModel()
) {
    val c = LocalAppTheme.colors
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography

    LaunchedEffect(viewModel) {
        viewModel.destination.collect { isFirstLaunch ->
            onFinished(isFirstLaunch)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        c.primaryAccent,
                        c.secondaryAccent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = c.onPrimary)
            Spacer(modifier = Modifier.height(d.lg))
            Text(
                text = stringResource(R.string.preloader_initializing),
                style = t.body,
                color = c.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OnboardingScreen1(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.lg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.onboarding_step1_title),
                style = t.h1
            )
            Spacer(modifier = Modifier.height(d.sm + d.xs))
            Text(
                text = stringResource(R.string.onboarding_step1_body),
                style = t.body
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNext
        ) {
            Text(stringResource(R.string.action_next))
        }
    }
}

@Composable
fun OnboardingScreen2(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.lg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.onboarding_step2_title),
                style = t.h1
            )
            Spacer(modifier = Modifier.height(d.sm + d.xs))
            Text(
                text = stringResource(R.string.onboarding_step2_body),
                style = t.body
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.completeOnboarding()
                onFinish()
            }
        ) {
            Text(stringResource(R.string.action_get_started))
        }
    }
}

@Composable
fun HomeScreen(
    onAddExpense: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenExpenses: () -> Unit,
    onOpenEvaluate: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val c = LocalAppTheme.colors
    val d = LocalAppTheme.dimens
    val r = LocalAppTheme.radius
    val t = LocalAppTheme.typography
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.md)
    ) {
        Text(
            text = stringResource(R.string.home_period_summary_title),
            style = t.h2
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(r.md),
            colors = CardDefaults.cardColors(containerColor = c.surface)
        ) {
            Column(
                modifier = Modifier.padding(d.md),
                verticalArrangement = Arrangement.spacedBy(d.sm)
            ) {
                Text(stringResource(R.string.home_total_expenses, state.totalExpenses), style = t.body)
                Text(stringResource(R.string.home_avg_satisfaction, state.averageSatisfaction), style = t.body)
                Text(stringResource(R.string.home_trend_placeholder), style = t.body)
            }
        }

        Text(
            text = stringResource(R.string.home_quick_actions_title),
            style = t.h3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(d.sm)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onAddExpense
            ) { Text(stringResource(R.string.action_add_expense)) }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onOpenEvaluate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = c.secondaryAccent
                )
            ) { Text(stringResource(R.string.action_evaluate_purchase)) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(d.sm)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onOpenAnalytics
            ) { Text(stringResource(R.string.action_open_analytics)) }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onOpenExpenses
            ) { Text(stringResource(R.string.action_open_expenses)) }
        }
    }
}

@Composable
fun AddExpenseScreen(
    onFinished: () -> Unit,
    viewModel: AddExpenseViewModel = koinViewModel()
) {
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect {
            onFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(
            text = stringResource(R.string.add_expense_title),
            style = t.h2
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.amountText,
            onValueChange = viewModel::onAmountChanged,
            label = { Text(stringResource(R.string.field_amount_label)) },
            isError = state.amountError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (state.amountError != null) {
            Text(
                text = stringResource(state.amountError!!.toAmountErrorResId()),
                style = t.caption
            )
        }

        CategoryDropdown(
            selectedId = state.category,
            onSelected = viewModel::onCategoryChanged,
            isError = state.categoryError != null
        )
        if (state.categoryError != null) {
            Text(
                text = stringResource(R.string.error_required_field),
                style = t.caption
            )
        }

        DateField(
            dateText = state.date.toUiDate(),
            onDateSelected = viewModel::onDateChanged,
            isError = state.dateError != null
        )
        if (state.dateError != null) {
            Text(
                text = stringResource(R.string.error_invalid_date_future),
                style = t.caption
            )
        }

        Text(
            text = stringResource(R.string.field_satisfaction_label),
            style = t.h3
        )
        Slider(
            value = state.satisfaction.toFloat(),
            onValueChange = { v -> viewModel.onSatisfactionChanged(v.toInt().coerceIn(1, 5)) },
            valueRange = 1f..5f,
            steps = 3
        )
        Text(
            text = state.satisfaction.toString(),
            style = t.body
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.commentText,
            onValueChange = viewModel::onCommentChanged,
            label = { Text(stringResource(R.string.field_comment_label)) },
            isError = state.commentError != null
        )
        if (state.commentError != null) {
            Text(
                text = stringResource(state.commentError!!.toErrorResId()),
                style = t.caption
            )
        }

        Spacer(modifier = Modifier.height(d.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(d.sm)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onFinished
            ) {
                Text(stringResource(R.string.action_cancel))
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = viewModel::submit,
                enabled = !state.isSaving
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
private fun FieldError.toErrorResId(): Int {
    return when (this) {
        FieldError.REQUIRED -> R.string.error_required_field
        FieldError.INVALID -> R.string.error_invalid_text
        FieldError.OUT_OF_RANGE -> R.string.error_number_out_of_range
        FieldError.TOO_LONG -> R.string.error_text_too_long
    }
}

@Composable
private fun FieldError.toAmountErrorResId(): Int {
    return when (this) {
        FieldError.REQUIRED -> R.string.error_required_field
        FieldError.INVALID -> R.string.error_invalid_number
        FieldError.OUT_OF_RANGE -> R.string.error_number_out_of_range
        FieldError.TOO_LONG -> R.string.error_text_too_long
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedId: String?,
    onSelected: (String) -> Unit,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = DefaultCategories.firstOrNull { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selected?.let { stringResource(it.labelRes) }.orEmpty(),
            onValueChange = {},
            label = { Text(stringResource(R.string.field_category_label)) },
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DefaultCategories.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.labelRes)) },
                    onClick = {
                        onSelected(option.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    dateText: String,
    onDateSelected: (java.time.LocalDate) -> Unit,
    isError: Boolean
) {
    var open by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { open = true },
        value = dateText,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.field_date_label)) },
        isError = isError,
        trailingIcon = { }
    )

    if (open) {
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = state.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(date)
                        }
                        open = false
                    }
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun ExpenseListScreen(
    onAddExpense: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    viewModel: ExpenseListViewModel = koinViewModel()
) {
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val r = LocalAppTheme.radius
    val c = LocalAppTheme.colors
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(
            text = stringResource(R.string.expense_list_title),
            style = t.h2
        )
        if (state.expenses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(r.md),
                colors = CardDefaults.cardColors(containerColor = c.surface)
            ) {
                Column(
                    modifier = Modifier.padding(d.md),
                    verticalArrangement = Arrangement.spacedBy(d.sm)
                ) {
                    Text(
                        text = stringResource(R.string.expense_list_empty_title),
                        style = t.h3
                    )
                    Text(
                        text = stringResource(R.string.expense_list_empty_body),
                        style = t.body
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(d.sm)
            ) {
                items(state.expenses, key = { it.id }) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDetail(expense.id) },
                        shape = RoundedCornerShape(r.md),
                        colors = CardDefaults.cardColors(containerColor = c.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(d.md),
                            verticalArrangement = Arrangement.spacedBy(d.xs)
                        ) {
                            Text(
                                text = "%.2f".format(expense.amount),
                                style = t.h3
                            )
                            Text(
                                text = DefaultCategories.firstOrNull { it.id == expense.category }
                                    ?.let { stringResource(it.labelRes) }
                                    ?: expense.category,
                                style = t.body
                            )
                            Text(
                                text = expense.date.toUiDate(),
                                style = t.caption
                            )
                            Text(
                                text = "${stringResource(R.string.field_satisfaction_label)}: ${expense.satisfaction}",
                                style = t.caption
                            )
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onAddExpense
        ) {
            Text(stringResource(R.string.action_add_expense))
        }
    }
}

@Composable
fun ExpenseDetailScreen(
    expenseId: Long,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: ExpenseDetailViewModel = koinViewModel()
) {
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect {
            onDeleted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(
            text = stringResource(R.string.expense_detail_title, expenseId),
            style = t.h2
        )
        val expense = state.expense
        if (expense == null) {
            Text(
                text = stringResource(R.string.expense_not_found),
                style = t.body
            )
        } else {
            Text(
                text = "%.2f".format(expense.amount),
                style = t.h3
            )
            Text(
                text = DefaultCategories.firstOrNull { it.id == expense.category }
                    ?.let { stringResource(it.labelRes) }
                    ?: expense.category,
                style = t.body
            )
            Text(
                text = expense.date.toUiDate(),
                style = t.caption
            )
            Text(
                text = "${stringResource(R.string.field_satisfaction_label)}: ${expense.satisfaction}",
                style = t.body
            )
            if (!expense.comment.isNullOrBlank()) {
                Text(
                    text = expense.comment,
                    style = t.body
                )
            }
        }

        Spacer(modifier = Modifier.height(d.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(d.sm)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onEdit(expenseId) },
                enabled = state.expense != null
            ) {
                Text(stringResource(R.string.action_edit))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = { showDeleteConfirm = true },
                enabled = state.expense != null
            ) {
                Text(stringResource(R.string.action_delete))
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_expense_title)) },
            text = { Text(stringResource(R.string.delete_expense_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.delete()
                    }
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun EditExpenseScreen(
    expenseId: Long,
    onFinished: () -> Unit,
    viewModel: EditExpenseViewModel = koinViewModel()
) {
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { onFinished() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(
            text = stringResource(R.string.edit_expense_title, expenseId),
            style = t.h2
        )
        if (state.isLoading) {
            Spacer(modifier = Modifier.height(d.md))
            CircularProgressIndicator()
            return@Column
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.amountText,
            onValueChange = viewModel::onAmountChanged,
            label = { Text(stringResource(R.string.field_amount_label)) },
            isError = state.amountError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (state.amountError != null) {
            Text(
                text = stringResource(state.amountError!!.toAmountErrorResId()),
                style = t.caption
            )
        }

        CategoryDropdown(
            selectedId = state.category,
            onSelected = viewModel::onCategoryChanged,
            isError = state.categoryError != null
        )
        if (state.categoryError != null) {
            Text(
                text = stringResource(R.string.error_required_field),
                style = t.caption
            )
        }

        DateField(
            dateText = state.date.toUiDate(),
            onDateSelected = viewModel::onDateChanged,
            isError = state.dateError != null
        )
        if (state.dateError != null) {
            Text(
                text = stringResource(R.string.error_invalid_date_future),
                style = t.caption
            )
        }

        Text(
            text = stringResource(R.string.field_satisfaction_label),
            style = t.h3
        )
        Slider(
            value = state.satisfaction.toFloat(),
            onValueChange = { v -> viewModel.onSatisfactionChanged(v.toInt().coerceIn(1, 5)) },
            valueRange = 1f..5f,
            steps = 3
        )
        Text(
            text = state.satisfaction.toString(),
            style = t.body
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.commentText,
            onValueChange = viewModel::onCommentChanged,
            label = { Text(stringResource(R.string.field_comment_label)) },
            isError = state.commentError != null
        )
        if (state.commentError != null) {
            Text(
                text = stringResource(state.commentError!!.toErrorResId()),
                style = t.caption
            )
        }

        Spacer(modifier = Modifier.height(d.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(d.sm)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onFinished
            ) {
                Text(stringResource(R.string.action_cancel))
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = viewModel::submit,
                enabled = !state.isSaving
            ) {
                Text(stringResource(R.string.action_save_and_back))
            }
        }
    }
}

@Composable
fun PurchaseEvaluationScreen() {
    val viewModel: PurchaseEvaluationViewModel = koinViewModel()
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val c = LocalAppTheme.colors
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(text = stringResource(R.string.evaluate_title), style = t.h2)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.plannedAmountText,
            onValueChange = viewModel::onPlannedAmountChanged,
            label = { Text(stringResource(R.string.evaluate_planned_amount_hint)) },
            isError = state.plannedAmountError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (state.plannedAmountError != null) {
            Text(text = stringResource(state.plannedAmountError!!.toAmountErrorResId()), style = t.caption)
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.expectedFrequencyText,
            onValueChange = viewModel::onExpectedFrequencyChanged,
            label = { Text(stringResource(R.string.evaluate_frequency_hint)) },
            isError = state.expectedFrequencyError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (state.expectedFrequencyError != null) {
            Text(text = stringResource(state.expectedFrequencyError!!.toAmountErrorResId()), style = t.caption)
        }
        Text(text = stringResource(R.string.evaluate_utility_label), style = t.body)
        Slider(
            value = state.expectedUtility.toFloat(),
            onValueChange = { viewModel.onExpectedUtilityChanged(it.toInt().coerceIn(1, 5)) },
            valueRange = 1f..5f,
            steps = 3
        )
        Text(text = state.expectedUtility.toString(), style = t.caption)
        Button(modifier = Modifier.fillMaxWidth(), onClick = viewModel::calculate) {
            Text(stringResource(R.string.evaluate_action_calculate))
        }
        state.result?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(d.sm),
                colors = CardDefaults.cardColors(containerColor = c.surface)
            ) {
                Column(modifier = Modifier.padding(d.md), verticalArrangement = Arrangement.spacedBy(d.xs)) {
                    Text(stringResource(R.string.evaluate_cost_per_use, result.costPerUse), style = t.body)
                    Text(stringResource(R.string.evaluate_value_index, result.valueIndex), style = t.body)
                    Text(stringResource(R.string.evaluate_comparison), style = t.h3)
                    Text(stringResource(result.comparisonText), style = t.body)
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen() {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val c = LocalAppTheme.colors
    val r = LocalAppTheme.radius
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(text = stringResource(R.string.analytics_title), style = t.h2)
        Text(text = stringResource(R.string.analytics_period), style = t.caption)
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.expenseCount == 0) {
            Text(text = stringResource(R.string.analytics_empty), style = t.body)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(r.md),
                colors = CardDefaults.cardColors(containerColor = c.surface)
            ) {
                Column(modifier = Modifier.padding(d.md), verticalArrangement = Arrangement.spacedBy(d.xs)) {
                    Text(stringResource(R.string.analytics_total, state.totalAmount, state.expenseCount), style = t.body)
                    Text(stringResource(R.string.analytics_avg_satisfaction, state.averageSatisfaction), style = t.body)
                }
            }
            Text(text = stringResource(R.string.analytics_by_category), style = t.h3)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(d.xs)) {
                items(state.categorySums, key = { it.categoryId }) { sum ->
                    val label = DefaultCategories.firstOrNull { it.id == sum.categoryId }
                        ?.let { stringResource(it.labelRes) } ?: sum.categoryId
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(r.sm),
                        colors = CardDefaults.cardColors(containerColor = c.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(d.sm),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, style = t.body)
                            Text(text = "%.2f (%d)".format(sum.totalAmount, sum.count), style = t.caption)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()
    val d = LocalAppTheme.dimens
    val t = LocalAppTheme.typography
    val context = LocalContext.current
    var showClearConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.clearDone.collect {
            showClearConfirm = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(d.md),
        verticalArrangement = Arrangement.spacedBy(d.sm)
    ) {
        Text(text = stringResource(R.string.settings_title), style = t.h2)
        Text(
            text = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME ?: "1.0"),
            style = t.caption
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val manager = ReviewManagerFactory.create(context)
                manager.requestReviewFlow().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val activity = context as? Activity
                        activity?.let { manager.launchReviewFlow(it, task.result) }
                    }
                }
            }
        ) {
            Text(stringResource(R.string.settings_rate_app))
        }
        val appName = stringResource(R.string.app_name)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, appName)
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            }
        ) {
            Text(stringResource(R.string.settings_share_app))
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showClearConfirm = true }
        ) {
            Text(stringResource(R.string.settings_clear_data))
        }
    }
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.settings_clear_data)) },
            text = { Text(stringResource(R.string.settings_clear_data_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}


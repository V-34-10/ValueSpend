package com.finance.valuespend.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finance.valuespend.R
import com.finance.valuespend.ui.screens.AddExpenseScreen
import com.finance.valuespend.ui.screens.AnalyticsScreen
import com.finance.valuespend.ui.screens.EditExpenseScreen
import com.finance.valuespend.ui.screens.ExpenseDetailScreen
import com.finance.valuespend.ui.screens.ExpenseListScreen
import com.finance.valuespend.ui.screens.HomeScreen
import com.finance.valuespend.ui.screens.OnboardingScreen1
import com.finance.valuespend.ui.screens.OnboardingScreen2
import com.finance.valuespend.ui.screens.PreloaderScreen
import com.finance.valuespend.ui.screens.PurchaseEvaluationScreen
import com.finance.valuespend.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    data object Preloader : Screen("preloader")
    data object Onboarding1 : Screen("onboarding/step1")
    data object Onboarding2 : Screen("onboarding/step2")
    data object Home : Screen("home")
    data object ExpenseList : Screen("expenses/list")
    data object AddExpense : Screen("expenses/add")
    data object ExpenseDetail : Screen("expenses/detail/{expenseId}") {
        fun create(expenseId: Long) = "expenses/detail/$expenseId"
        const val ARG_ID = "expenseId"
    }

    data object EditExpense : Screen("expenses/edit/{expenseId}") {
        fun create(expenseId: Long) = "expenses/edit/$expenseId"
        const val ARG_ID = "expenseId"
    }

    data object Analytics : Screen("analytics")
    data object Evaluate : Screen("evaluate")
    data object Settings : Screen("settings")
}

enum class MainTab(val route: String, val labelRes: Int) {
    HOME(Screen.Home.route, R.string.tab_home),
    EXPENSES(Screen.ExpenseList.route, R.string.tab_expenses),
    ANALYTICS(Screen.Analytics.route, R.string.tab_analytics),
    EVALUATE(Screen.Evaluate.route, R.string.tab_evaluate),
    SETTINGS(Screen.Settings.route, R.string.tab_settings)
}

@Composable
fun ValueSpendRoot() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val tabs = MainTab.values().toList()

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Preloader.route &&
                currentRoute != Screen.Onboarding1.route &&
                currentRoute != Screen.Onboarding2.route
            ) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute?.startsWith(tab.route) == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(Screen.Home.route) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            },
                            label = { Text(stringResource(tab.labelRes)) },
                            icon = { }
                        )
                    }
                }
            }
        }
    ) { padding ->
        ValueSpendNavHost(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun ValueSpendNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Preloader.route,
        modifier = modifier
    ) {
        composable(Screen.Preloader.route) {
            PreloaderScreen(
                onFinished = { isFirstLaunch ->
                    if (isFirstLaunch) {
                        navController.navigate(Screen.Onboarding1.route) {
                            popUpTo(Screen.Preloader.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Preloader.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Onboarding1.route) {
            OnboardingScreen1(
                onNext = { navController.navigate(Screen.Onboarding2.route) }
            )
        }
        composable(Screen.Onboarding2.route) {
            OnboardingScreen2(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding1.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onAddExpense = { navController.navigate(Screen.AddExpense.route) },
                onOpenAnalytics = { navController.navigate(Screen.Analytics.route) },
                onOpenExpenses = { navController.navigate(Screen.ExpenseList.route) },
                onOpenEvaluate = { navController.navigate(Screen.Evaluate.route) }
            )
        }
        composable(Screen.ExpenseList.route) {
            ExpenseListScreen(
                onAddExpense = { navController.navigate(Screen.AddExpense.route) },
                onOpenDetail = { id ->
                    navController.navigate(Screen.ExpenseDetail.create(id))
                }
            )
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onFinished = { navController.popBackStack() }
            )
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
        composable(Screen.Evaluate.route) {
            PurchaseEvaluationScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Screen.ExpenseDetail.route,
            arguments = listOf(
                navArgument(Screen.ExpenseDetail.ARG_ID) { type = NavType.LongType }
            )
        ) { entry ->
            val id = entry.arguments?.getLong(Screen.ExpenseDetail.ARG_ID) ?: return@composable
            ExpenseDetailScreen(
                expenseId = id,
                onEdit = { navController.navigate(Screen.EditExpense.create(id)) },
                onDeleted = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument(Screen.EditExpense.ARG_ID) { type = NavType.LongType }
            )
        ) { entry ->
            val id = entry.arguments?.getLong(Screen.EditExpense.ARG_ID) ?: return@composable
            EditExpenseScreen(
                expenseId = id,
                onFinished = { navController.popBackStack() }
            )
        }
    }
}


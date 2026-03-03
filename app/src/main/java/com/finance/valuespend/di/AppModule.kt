package com.finance.valuespend.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.finance.valuespend.data.datastore.AppPreferences
import com.finance.valuespend.data.datastore.DataStoreAppPreferences
import com.finance.valuespend.data.db.AppDatabase
import com.finance.valuespend.data.repository.ExpenseRepository
import com.finance.valuespend.data.repository.RoomExpenseRepository
import androidx.lifecycle.SavedStateHandle
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
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("app_preferences") }
        )
    }

    single<AppPreferences> { DataStoreAppPreferences(get()) }

    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "valuespend.db"
        ).build()
    }

    single { get<AppDatabase>().expenseDao() }
    single<ExpenseRepository> { RoomExpenseRepository(get()) }

    viewModel { PreloaderViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AddExpenseViewModel(get()) }
    viewModel { ExpenseListViewModel(get()) }
    viewModel { (handle: SavedStateHandle) -> ExpenseDetailViewModel(handle, get()) }
    viewModel { (handle: SavedStateHandle) -> EditExpenseViewModel(handle, get()) }
    viewModel { PurchaseEvaluationViewModel(get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}


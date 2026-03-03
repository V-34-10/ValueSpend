package com.finance.valuespend.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreAppPreferences(
    private val dataStore: DataStore<Preferences>
) : AppPreferences {
    private object Keys {
        val OnboardingCompleted = booleanPreferencesKey("onboarding_completed")
    }

    override val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[Keys.OnboardingCompleted] ?: false }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.OnboardingCompleted] = completed
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}


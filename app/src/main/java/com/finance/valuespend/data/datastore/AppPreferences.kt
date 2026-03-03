package com.finance.valuespend.data.datastore

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val onboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun clearAll()
}


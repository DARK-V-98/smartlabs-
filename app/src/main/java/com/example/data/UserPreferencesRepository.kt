package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.smartLabsDataStore: DataStore<Preferences> by preferencesDataStore(name = "smartlabs_prefs")

data class LocalAppPreferences(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val biometricLockEnabled: Boolean = false,
    val remindersEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val onboardingSeen: Boolean = false,
    val simulateLowSignalSkeleton: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_preference")
        val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock_enabled")
        val REMINDERS = booleanPreferencesKey("reminders_enabled")
        val SOUND = booleanPreferencesKey("sound_effects_enabled")
        val ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
        val LOW_SIGNAL_SKELETON = booleanPreferencesKey("low_signal_skeleton")
    }

    val preferences: Flow<LocalAppPreferences> = context.smartLabsDataStore.data.map { prefs ->
        val themeName = prefs[Keys.THEME] ?: ThemePreference.SYSTEM.name
        val theme = ThemePreference.entries.firstOrNull { it.name == themeName } ?: ThemePreference.SYSTEM
        LocalAppPreferences(
            themePreference = theme,
            biometricLockEnabled = prefs[Keys.BIOMETRIC_LOCK] ?: false,
            remindersEnabled = prefs[Keys.REMINDERS] ?: true,
            soundEffectsEnabled = prefs[Keys.SOUND] ?: true,
            onboardingSeen = prefs[Keys.ONBOARDING_SEEN] ?: false,
            simulateLowSignalSkeleton = prefs[Keys.LOW_SIGNAL_SKELETON] ?: false
        )
    }

    suspend fun setThemePreference(theme: ThemePreference) {
        context.smartLabsDataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun setBiometricLockEnabled(enabled: Boolean) {
        context.smartLabsDataStore.edit { it[Keys.BIOMETRIC_LOCK] = enabled }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.smartLabsDataStore.edit { it[Keys.REMINDERS] = enabled }
    }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.smartLabsDataStore.edit { it[Keys.SOUND] = enabled }
    }

    suspend fun setOnboardingSeen(seen: Boolean) {
        context.smartLabsDataStore.edit { it[Keys.ONBOARDING_SEEN] = seen }
    }

    suspend fun setSimulateLowSignalSkeleton(enabled: Boolean) {
        context.smartLabsDataStore.edit { it[Keys.LOW_SIGNAL_SKELETON] = enabled }
    }
}

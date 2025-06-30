// app/src/main/java/me/daltonbsf/unirun/data/UserPreferences.kt

package me.daltonbsf.unirun.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences private constructor(private val context: Context) {

    companion object {
        const val THEME_KEY = "is_dark_theme"
        const val ALL_NOTIFICATIONS_KEY = "all_notifications_enabled"
        const val USER_NOTIFICATIONS_KEY = "user_notifications_enabled"
        const val CARONA_NOTIFICATIONS_KEY = "carona_notifications_enabled"
        const val RIDE_LEAVING_NOTIFICATIONS_KEY = "ride_leaving_notifications_enabled"
        const val GROUP_ENTRY_EXIT_NOTIFICATIONS_KEY = "group_entry_exit_notifications_enabled"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return instance ?: synchronized(this) {
                instance ?: UserPreferences(context.applicationContext).also { instance = it }
            }
        }
    }

    suspend fun savePreference(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    fun getPreference(key: String, defaultValue: String): Flow<String> {
        val dataStoreKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey] ?: defaultValue
        }
    }
}
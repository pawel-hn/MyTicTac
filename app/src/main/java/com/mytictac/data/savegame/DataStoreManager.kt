package com.mytictac.data.savegame

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import javax.inject.Inject

const val SAVE_GAME_PREF_NAME = "saved"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SAVE_GAME_PREF_NAME
)

interface DataStoreManager {

    enum class PreferenceKey {
        SAVED_GAME
    }

    suspend fun store(preferenceKey: PreferenceKey, value: String): Result<Unit>
    suspend fun get(preferenceKey: PreferenceKey): Result<String?>
}

class AndroidDataStoreManager @Inject constructor(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : DataStoreManager {

    override suspend fun store(
        preferenceKey: DataStoreManager.PreferenceKey,
        value: String
    ): Result<Unit> = coroutineScope.async {
        return@async try {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(preferenceKey.name)] = value
            }
            Result.success(Unit)
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }.await()

    override suspend fun get(
        preferenceKey: DataStoreManager.PreferenceKey
    ): Result<String?> = coroutineScope.async {
        try {
            val preferences = context.dataStore.data.first()
            Result.success(preferences[stringPreferencesKey(preferenceKey.name)])
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }.await()

}
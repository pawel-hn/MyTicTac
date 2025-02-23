package com.mytictac.data.savegame

import com.mytictac.data.SaveGame
import javax.inject.Inject
import kotlinx.serialization.json.Json

interface LoadGameUseCase {
    suspend fun invoke(): Result<SaveGame>
}

class AndroidLoadGameUseCase
@Inject
constructor(
    private val dataStoreManager: DataStoreManager,
    private val json: Json = Json
) : LoadGameUseCase {
    override suspend fun invoke(): Result<SaveGame> {
        return try {
            dataStoreManager.get(
                preferenceKey = DataStoreManager.PreferenceKey.SAVED_GAME
            ).getOrNull()?.let {
                val savedGame = json.decodeFromString<SaveGame>(it)
                Result.success(savedGame)
            } ?: Result.failure(Exception("Game not loaded."))
        } catch (e: Exception) {
            Result.failure(Exception("Game not loaded."))
        }
    }
}

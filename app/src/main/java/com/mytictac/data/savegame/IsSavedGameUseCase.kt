package com.mytictac.data.savegame

import javax.inject.Inject

interface IsSavedGameUseCase {
    suspend fun invoke(): Boolean
}

class AndroidIsSavedGameUseCase
@Inject
constructor(
    private val dataStoreManager: DataStoreManager
) : IsSavedGameUseCase {
    override suspend fun invoke(): Boolean {
        return try {
            val savedGame =
                dataStoreManager.get(DataStoreManager.PreferenceKey.SAVED_GAME).getOrNull()
            savedGame != null
        } catch (e: Exception) {
            false
        }
    }
}

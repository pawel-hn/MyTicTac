package com.mytictac.di

import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.savegame.LoadGameUseCase
import com.mytictac.data.savegame.SaveGameUseCase
import com.mytictac.gameengine.AndroidGameEngine
import com.mytictac.gameengine.GameEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object GameEngineModule {
    @ViewModelScoped
    @Provides
    fun provideGameEngine(
        gameOptionsService: GameOptionsService,
        saveGameUseCase: SaveGameUseCase,
        loadGameUseCase: LoadGameUseCase
    ): GameEngine = AndroidGameEngine(gameOptionsService, saveGameUseCase, loadGameUseCase)
}

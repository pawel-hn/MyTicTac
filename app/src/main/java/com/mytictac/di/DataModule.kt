package com.mytictac.di

import android.content.Context
import com.mytictac.data.gameoptions.AndroidGameOptionsService
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.savegame.AndroidDataStoreManager
import com.mytictac.data.savegame.AndroidIsSavedGameUseCase
import com.mytictac.data.savegame.DataStoreManager
import com.mytictac.data.savegame.IsSavedGameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideGameOptions(): GameOptionsService = AndroidGameOptionsService()

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext appContext: Context,
    ): DataStoreManager = AndroidDataStoreManager(appContext)
}


@Module
@InstallIn(ViewModelComponent::class)
object DataModuleScoped {

    @ViewModelScoped
    @Provides
    fun provideIsSavedGameUseCase(
        dataStoreManager: DataStoreManager
    ): IsSavedGameUseCase = AndroidIsSavedGameUseCase(dataStoreManager)

}
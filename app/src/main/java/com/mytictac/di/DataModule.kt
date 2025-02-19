package com.mytictac.di

import android.content.Context
import com.mytictac.data.gameoptions.AndroidGameOptionsService
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.savegame.AndroidDataStoreManager
import com.mytictac.data.savegame.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
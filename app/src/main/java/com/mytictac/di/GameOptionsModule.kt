package com.mytictac.di

import com.mytictac.data.gameoptions.AndroidGameOptionsService
import com.mytictac.data.gameoptions.GameOptionsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GameOptionsModule {

    @Provides
    @Singleton
    fun provideGameOptions(): GameOptionsService = AndroidGameOptionsService()
}
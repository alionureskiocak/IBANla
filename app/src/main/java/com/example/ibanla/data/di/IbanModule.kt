package com.example.ibanla.data.di

import android.content.Context
import androidx.room.Room
import com.example.ibanla.data.local.IbanDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IbanModule {

    @Provides
    @Singleton
    fun provideIbanDatabase(@ApplicationContext context : Context) : IbanDatabase{
        return Room.databaseBuilder(
            context,
            IbanDatabase::class.java,
            "iban_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideIbanDao(db : IbanDatabase) = db.ibanDao()
}
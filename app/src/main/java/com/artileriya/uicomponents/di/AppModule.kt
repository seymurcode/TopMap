package com.artileriya.uicomponents.di

import android.content.Context
import androidx.room.Room
import com.artileriya.uicomponents.dao.UserDao
import com.artileriya.uicomponents.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideUserDao(appDatabase : AppDatabase) : UserDao {
        return appDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context : Context) : AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "demodb").build()
    }
}

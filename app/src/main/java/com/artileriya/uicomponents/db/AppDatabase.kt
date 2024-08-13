package com.artileriya.uicomponents.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.artileriya.uicomponents.dao.UserDao
import com.artileriya.uicomponents.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var database : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            if(database != null) {
                return database!!
            } else {
                synchronized(this) {
                    database = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "demodb").build()

                    return database!!
                }
            }
        }
    }
}
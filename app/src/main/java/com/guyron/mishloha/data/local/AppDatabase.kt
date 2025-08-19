package com.guyron.mishloha.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.guyron.mishloha.data.local.dao.RepositoryDao
import com.guyron.mishloha.data.local.entity.RepositoryEntity
import com.guyron.mishloha.data.local.converter.DateConverter

@Database(
    entities = [RepositoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "github_repositories_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

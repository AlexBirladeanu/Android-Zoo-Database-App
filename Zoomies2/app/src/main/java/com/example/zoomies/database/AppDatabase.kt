package com.example.zoomies.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zoomies.database.dao.AnimalDAO
import com.example.zoomies.database.dao.UserDAO
import com.example.zoomies.database.entity.Animal
import com.example.zoomies.database.entity.User

@Database(entities = [User::class, Animal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun animalDao(): AnimalDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            INSTANCE = null
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zoomies-database"
                ).build()
                INSTANCE = instance
                return  instance
            }
        }
    }
}
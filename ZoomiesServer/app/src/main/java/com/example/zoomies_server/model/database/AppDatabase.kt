package com.example.zoomies_server.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zoomies_server.model.database.dao.AnimalDAO
import com.example.zoomies_server.model.database.dao.UserDAO
import com.example.zoomies_server.database.dto.AnimalDTO
import com.example.zoomies_server.model.database.entity.Animal
import com.example.zoomies_server.model.database.entity.User
import com.example.zoomies_server.model.database.entity.UserRole
import com.example.zoomies_server.model.dto.DtoUtils
import com.example.zoomies_server.model.dto.UserDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Database(entities = [User::class, Animal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun animalDao(): AnimalDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance == null) {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "zoomies-database"
                    ).build()
                    INSTANCE = instance
                    CoroutineScope(Dispatchers.IO + Job()).launch {
                        users = instance.userDao().getAll()?.map { DtoUtils.fromUserEntity(it) }
                        if (users.isNullOrEmpty()) {
                            instance.userDao()
                                .insert(User(-1, "admin", "admin", UserRole.ADMIN, "", ""))
                        }
                        animals =
                            instance.animalDao().getAll()?.map { DtoUtils.fromAnimalEntity(it) }
                    }
                    return instance
                }
            } else {
                CoroutineScope(Dispatchers.IO + Job()).launch {
                    users = tempInstance.userDao().getAll()?.map { DtoUtils.fromUserEntity(it) }
                    if (users.isNullOrEmpty()) {
                        tempInstance.userDao()
                            .insert(User(-1, "admin", "admin", UserRole.ADMIN, "", ""))
                    }
                    animals =
                        tempInstance.animalDao().getAll()?.map { DtoUtils.fromAnimalEntity(it) }
                }
                return tempInstance
            }
        }

        var animals: List<AnimalDTO>? = null
        var users: List<UserDTO>? = null
    }
}
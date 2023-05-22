package com.example.zoomies

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.zoomies.model.database.AppDatabase
import com.example.zoomies.model.database.entity.Animal
import com.example.zoomies.model.database.entity.User
import com.example.zoomies.model.database.entity.UserRole
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest : TestCase() {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()

    }

    @After
    fun destroy() {
        database.close()
    }

    @Test
    fun writeAnimal() = runBlocking {
        val newAnimal = Animal(
            animalId = null, name = "Tiger", species = "feline", habitat = "jungle", diet = "Mowgli"
        )
        database.animalDao().insert(newAnimal)
        assertEquals(1, database.animalDao().getAll()?.size)
    }

    @Test
    fun readAnimal() = runBlocking {
        val animal = Animal(
            animalId = null,
            name = "Lion",
            species = "feline",
            habitat = "savannah",
            diet = "zebras"
        )
        database.animalDao().insert(animal)
        val allAnimals = database.animalDao().getAll()!!
        val foundAnimal = allAnimals.filter { it.name == animal.name }

        assertEquals(1, foundAnimal.size)

        val found = foundAnimal.first()

        assert(
            found.name == animal.name && found.species == animal.species && found.habitat == animal.habitat && found.diet == animal.diet
        )
    }

    @Test
    fun deleteAnimal() = runBlocking {
        val newAnimal = Animal(
            animalId = null,
            name = "Leopard",
            species = "feline",
            habitat = "jungle",
            diet = "lemurs"
        )
        database.animalDao().insert(newAnimal)
        assertEquals(1, database.animalDao().getAll()?.size)
        val foundAnimal = database.animalDao().getAll()!!.first()
        database.animalDao().delete(foundAnimal)
        assert(database.animalDao().getAll().isNullOrEmpty())
    }

    @Test
    fun writeUser() = runBlocking {
        val newUser = User(
            uid = null,
            userName = "admin",
            password = "admin",
            email = "admin@gmail.com",
            role = UserRole.ADMIN,
            phoneNumber = "076251427"
        )
        database.userDao().insert(newUser)
        assertEquals(1, database.userDao().getAll()?.size)
    }

    @Test
    fun readUser() = runBlocking {
        val newUser = User(
            uid = null,
            userName = "admin",
            password = "admin",
            email = "admin@gmail.com",
            role = UserRole.ADMIN,
            phoneNumber = "076251427"
        )
        database.userDao().insert(newUser)
        val allUsers = database.userDao().getAll()!!
        val foundUser = allUsers.filter { it.userName == newUser.userName }
        assertEquals(1, foundUser.size)
        val found = foundUser.first()
        assert(
            found.userName == newUser.userName && found.password == newUser.password && found.email == newUser.email && found.role == newUser.role && found.phoneNumber == newUser.phoneNumber
        )

    }

    @Test
    fun deleteUser() = runBlocking {
        val newUser = User(
            uid = null,
            userName = "admin",
            password = "admin",
            email = "admin@gmail.com",
            role = UserRole.ADMIN,
            phoneNumber = "076251427"
        )
        database.userDao().insert(newUser)
        assertEquals(1, database.userDao().getAll()?.size)
        val foundUser = database.userDao().getAll()!!.first()
        database.userDao().delete(foundUser)
        assert(database.userDao().getAll().isNullOrEmpty())
    }
}
package com.example.zoomies.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zoomies.database.entity.Animal

@Dao
interface AnimalDAO {
    @Query("SELECT * FROM animal")
    fun getAll(): List<Animal>?

    @Query("SELECT * FROM animal WHERE name LIKE :name "
            //+ "LIMIT 1"
    )
    fun findByName(name: String): List<Animal>?

    @Insert
    fun insertAll(vararg animals: Animal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(animal: Animal)

    @Delete
    fun delete(animal: Animal)
}
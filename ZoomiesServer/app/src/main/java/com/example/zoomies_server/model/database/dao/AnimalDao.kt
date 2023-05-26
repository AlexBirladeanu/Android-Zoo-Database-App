package com.example.zoomies_server.model.database.dao

import androidx.room.*
import com.example.zoomies_server.model.database.entity.Animal

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
package com.example.zoomies_server.model.database.dao

import androidx.room.*
import com.example.zoomies_server.model.database.entity.User

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>?

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE username LIKE :username " +
            "LIMIT 1")
    fun findByName(username: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)
}
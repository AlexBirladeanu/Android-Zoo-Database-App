package com.example.zoomies.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "username") val userName: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "role") val role: UserRole,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
)

enum class UserRole {
    VISITOR,
    EMPLOYEE,
    ADMIN
}
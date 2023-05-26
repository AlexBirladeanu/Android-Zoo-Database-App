package com.example.zoomies_server.model.dto

import com.example.zoomies_server.database.dto.GeneralDTO
import com.example.zoomies_server.model.database.entity.User
import com.example.zoomies_server.model.database.entity.UserRole

class UserDTO(
    val uid: Int?,
    val userName: String,
    val password: String,
    val role: UserRole,
    val email: String,
    val phoneNumber: String,
): GeneralDTO() {
    init {
        super.uidS = uid
        super.userNameS = userName
        super.passwordS = password
        super.roleS = role
        super.emailS = email
        super.phoneNumberS = phoneNumber
    }

    override fun toString(): String {
        return "UserDTO(uid=$uid, userName='$userName', password='$password', role=$role, email='$email', phoneNumber='$phoneNumber')"
    }
}
package com.example.zoomies.model.dto

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

enum class UserRole {
    VISITOR,
    EMPLOYEE,
    ADMIN
}
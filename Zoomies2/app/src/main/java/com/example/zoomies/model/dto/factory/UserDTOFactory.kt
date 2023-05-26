package com.example.zoomies.model.dto.factory

import com.example.zoomies.model.dto.UserDTO
import com.example.zoomies.model.dto.UserRole

class UserDTOFactory : DTOFactory<UserDTO> {
    override fun createDTO(
        animalId: Int?,
        name: String,
        species: String,
        habitat: String,
        diet: String,
        uid: Int?,
        userName: String,
        password: String,
        role: UserRole,
        email: String,
        phoneNumber: String
    ): UserDTO {
        return UserDTO(
            uid = uid,
            userName = userName,
            password = password,
            role = role,
            email = email,
            phoneNumber = phoneNumber
        )
    }

    companion object {
        val instance: DTOFactory<UserDTO> = UserDTOFactory()
    }
}
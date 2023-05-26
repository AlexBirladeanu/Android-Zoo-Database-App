package com.example.zoomies_server.model.dto.factory

import com.example.zoomies_server.database.dto.GeneralDTO
import com.example.zoomies_server.model.database.entity.UserRole

interface DTOFactory<T : GeneralDTO> {
    fun createDTO(
        animalId: Int? = null,
        name: String = "",
        species: String = "",
        habitat: String = "",
        diet: String = "",
        uid: Int? = null,
        userName: String = "",
        password: String = "",
        role: UserRole = UserRole.EMPLOYEE,
        email: String = "",
        phoneNumber: String = "",
    ): T
}
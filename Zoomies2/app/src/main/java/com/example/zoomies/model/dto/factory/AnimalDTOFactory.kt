package com.example.zoomies.model.dto.factory

import com.example.zoomies.model.dto.AnimalDTO
import com.example.zoomies.model.dto.UserRole

class AnimalDTOFactory : DTOFactory<AnimalDTO> {
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
    ): AnimalDTO {
        return AnimalDTO(
            animalId = animalId,
            name = name,
            species = species,
            habitat = habitat,
            diet = diet
        )
    }

    companion object {
        val instance: DTOFactory<AnimalDTO> = AnimalDTOFactory()
    }
}
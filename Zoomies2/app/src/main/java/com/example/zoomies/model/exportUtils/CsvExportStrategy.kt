package com.example.zoomies.model.exportUtils

import com.example.zoomies.model.dto.AnimalDTO
import java.io.File
import java.io.FileOutputStream

class CsvExportStrategy : ExportStrategy {
    override fun export(file: File, fileContent: List<AnimalDTO>) {
        val outputStream = FileOutputStream(file)
        val writer = outputStream.bufferedWriter()
        writer.write(""""ID", "Name", "Species", "Habitat", "Diet"""")
        writer.newLine()
        fileContent.forEach {
            writer.write("${it.animalId}, ${it.name}, ${it.species}, ${it.habitat},\"${it.diet}\"")
            writer.newLine()
        }
        writer.flush()
        outputStream.close()
    }
}
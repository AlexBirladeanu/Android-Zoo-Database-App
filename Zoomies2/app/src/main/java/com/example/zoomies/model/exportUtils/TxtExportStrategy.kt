package com.example.zoomies.model.exportUtils

import com.example.zoomies.model.dto.AnimalDTO
import java.io.File
import java.io.FileOutputStream

class TxtExportStrategy : ExportStrategy {
    override fun export(file: File, fileContent: List<AnimalDTO>) {
        val outputStream = FileOutputStream(file)
        outputStream.write(fileContent.toString().encodeToByteArray())
        outputStream.close()
    }
}
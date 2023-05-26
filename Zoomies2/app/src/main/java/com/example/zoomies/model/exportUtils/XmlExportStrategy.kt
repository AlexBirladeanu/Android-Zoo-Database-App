package com.example.zoomies.model.exportUtils

import android.util.Xml
import com.example.zoomies.model.dto.AnimalDTO
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileOutputStream

class XmlExportStrategy : ExportStrategy {
    override fun export(file: File, fileContent: List<AnimalDTO>) {
        val serializer: XmlSerializer = Xml.newSerializer()
        val outputStream = FileOutputStream(file)
        serializer.setOutput(outputStream, "UTF-8")
        serializer.startDocument(null, true)
        serializer.startTag(null, "Animals")

        fileContent.forEach { animal ->
            serializer.startTag(null, "Animal")
            serializer.attribute(null, "id", animal.animalId.toString())
            serializer.attribute(null, "name", animal.name)
            serializer.attribute(null, "species", animal.species)
            serializer.attribute(null, "habitat", animal.habitat)
            serializer.attribute(null, "diet", animal.diet)
            serializer.endTag(null, "Animal")
        }

        serializer.endTag(null, "Animals")
        serializer.endDocument()
        outputStream.flush()
        outputStream.close()
    }
}
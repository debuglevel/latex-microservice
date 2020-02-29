package de.debuglevel.latex.storage

import de.debuglevel.latex.LatexRequest
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.inject.Singleton

@Singleton
class DocumentStorage {
    private val logger = KotlinLogging.logger {}

    private val documents = mutableMapOf<UUID, StoredFile>()

    fun get(uuid: UUID): StoredFile {
        logger.debug { "Getting data for ID '$uuid' from storage..." }

        val temporaryDirectory = Files.createTempDirectory("latex-microservice")
        logger.debug { "Using temporary directory $temporaryDirectory" }

        val storedFile =
            documents[uuid]?.copy(path = temporaryDirectory) ?: throw DocumentNotFoundException(
                uuid
            )

        storedFile.files.forEach {
            logger.debug { "Creating file '${it.name}'..." }

            val path = temporaryDirectory.resolve(it.name)

            // check for directory traversal attack
            if (!path.isChild(temporaryDirectory)) {
                throw InvalidPathException(it.name)
            }

            if (path.parent != null) {
                Files.createDirectories(path.parent)
            }

            val file = path.toFile()
            logger.debug { "Writing file: '${file.absolutePath}'..." }
            file.writeBytes(it.asByteArray)
        }

        return storedFile
    }

    fun add(latexRequest: LatexRequest): StoredFile {
        val uuid = UUID.randomUUID()
        val storedFile = StoredFile(
            latexRequest.files,
            uuid,
            null,
            latexRequest.requiredPackages
        )
        documents[uuid] = storedFile
        return storedFile
    }

    fun remove(uuid: UUID): Boolean {
        val storedFile = documents.remove(uuid)
        return storedFile != null
    }

    class DocumentNotFoundException(uuid: UUID) : Exception("Document '$uuid' does not exist")
    class InvalidPathException(filename: String) : Exception("Filename or path '$filename' is invalid")

    private fun Path.isChild(parent: Path): Boolean {
        val absoluteParent = parent.toAbsolutePath().normalize()
        val absoluteChild = this.toAbsolutePath().normalize()
        val isChild = absoluteChild.startsWith(absoluteParent)

        logger.trace { "$absoluteChild is child of $absoluteParent: $isChild" }

        return isChild
    }
}
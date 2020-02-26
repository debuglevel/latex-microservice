package de.debuglevel.latex.domain.latex

import de.debuglevel.latex.rest.latex.LatexRequestDTO
import de.debuglevel.latex.rest.latex.StoredFileTransferDTO
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object DocumentStorage {
    private val logger = KotlinLogging.logger {}

    private val documents = mutableMapOf<UUID, StoredFileTransferDTO>()

    fun get(uuid: UUID): StoredFileTransferDTO {
        logger.debug { "Getting data for ID '$uuid' from storage..." }

        val temporaryDirectory = Files.createTempDirectory("latex-microservice")
        logger.debug { "Using temporary directory $temporaryDirectory" }

        val storedFileTransferDTO =
            documents[uuid]?.copy(path = temporaryDirectory) ?: throw DocumentNotFoundException(uuid)

        storedFileTransferDTO.files.forEach {
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

        return storedFileTransferDTO
    }

    fun add(latexRequest: LatexRequestDTO): StoredFileTransferDTO {
        val uuid = UUID.randomUUID()
        val markdownWithUUID = StoredFileTransferDTO(latexRequest.files, uuid, null, latexRequest.requiredPackages)
        documents[uuid] = markdownWithUUID
        return markdownWithUUID
    }

    fun remove(uuid: UUID): Boolean {
        val storedFileTransferDTO = documents.remove(uuid)
        return storedFileTransferDTO != null
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
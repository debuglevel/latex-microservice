package de.debuglevel.latex.rest.latex

import java.nio.file.Path
import java.util.*

data class StoredFileTransferDTO(
    val files: Array<FileDTO>,
    val uuid: UUID,
    val path: Path?,
    val requiredPackages: Array<String>?
)
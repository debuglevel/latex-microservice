package de.debuglevel.latex.storage

import java.nio.file.Path
import java.util.*

data class StoredFile(
    val files: Array<TransferFile>,
    val uuid: UUID,
    val path: Path?,
    val requiredPackages: Array<String>?
)
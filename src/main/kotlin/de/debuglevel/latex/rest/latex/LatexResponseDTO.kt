package de.debuglevel.latex.rest.latex

data class LatexResponseDTO(
    val success: Boolean,
    val exitValue: Int,
    val durationMilliseconds: Long,
    val files: Array<FileDTO>,
    val output: String
)
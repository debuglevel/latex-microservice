package de.debuglevel.latex.rest.latex

/**
 * @param requiredPackages Packages to install by MikTex' mpm tool before executing the pdflatex compiler
 * @param blocking Whether the POST call should block, return the result and not save it in storage
 */
data class LatexRequestDTO(
    val requiredPackages: Array<String>,
    val files: Array<FileDTO>,
    val blocking: Boolean = false
)
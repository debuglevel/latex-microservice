package de.debuglevel.latex.rest.latex

/**
 * @param requiredPackages Packages to install by MikTex' mpm tool before executing the pdflatex compiler
 */
data class LatexRequestDTO(
    val requiredPackages: Array<String>,
    val files: Array<FileDTO>
)
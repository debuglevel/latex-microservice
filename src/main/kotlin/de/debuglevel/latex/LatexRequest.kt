package de.debuglevel.latex

import de.debuglevel.latex.storage.TransferFile

/**
 * @param requiredPackages Packages to install by MikTex' mpm tool before executing the pdflatex compiler
 * @param blocking Whether the POST call should block, return the result and not save it in storage
 */
data class LatexRequest(
    val requiredPackages: Array<String>,
    val files: Array<TransferFile>,
    val blocking: Boolean = false
)
package de.debuglevel.latex

import de.debuglevel.latex.storage.TransferFile
import de.debuglevel.latex.compiler.LatexPdfCompilerResult
import de.debuglevel.latex.util.toBase64
import java.util.*

data class LatexResponse(
    val success: Boolean,
    val exitValue: Int,
    val durationMilliseconds: Long,
    val files: Array<TransferFile>,
    val output: String,
    val packagemanagerSuccess: Boolean?,
    val packagemanagerExitValue: Int?,
    val packagemanagerDurationMilliseconds: Long?,
    val packagemanagerOutput: String?
) {
    constructor(compilerResult: LatexPdfCompilerResult) : this(
        compilerResult.success,
        compilerResult.exitValue,
        compilerResult.durationMilliseconds,
        compilerResult.files.map {
            TransferFile(
                compilerResult.workingDirectory.relativize(it).toString(),
                it.toFile().readBytes().toBase64()
            )
        }.toTypedArray(),
        compilerResult.output,
        compilerResult.packagemanagerSuccess,
        compilerResult.packagemanagerExitValue,
        compilerResult.packagemanagerDurationMilliseconds,
        compilerResult.packagemanagerOutput
    )
}
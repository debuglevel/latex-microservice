package de.debuglevel.latex.compiler

import de.debuglevel.latex.command.CommandResult
import java.nio.file.Path

data class LatexPdfCompilerResult(
    val success: Boolean,
    val exitValue: Int,
    val durationMilliseconds: Long,
    val files: Array<Path>,
    val output: String,
    val packagemanagerSuccess: Boolean?,
    val packagemanagerExitValue: Int?,
    val packagemanagerDurationMilliseconds: Long?,
    val packagemanagerOutput: String?,
    val workingDirectory: Path
) {
    constructor(
        latexCommandResult: CommandResult,
        files: Array<Path>,
        mpmCommandResult: CommandResult?,
        workingDirectory: Path
    ) : this(
        latexCommandResult.successful,
        latexCommandResult.exitValue,
        latexCommandResult.durationMilliseconds,
        files,
        latexCommandResult.output,
        mpmCommandResult?.successful,
        mpmCommandResult?.exitValue,
        mpmCommandResult?.durationMilliseconds,
        mpmCommandResult?.output,
        workingDirectory
    )
}
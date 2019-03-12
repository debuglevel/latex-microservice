package de.debuglevel.latex.domain.latex

import de.debuglevel.latex.domain.command.CommandResult
import java.nio.file.Path

data class LatexResult(
    val success: Boolean,
    val exitValue: Int,
    val durationMilliseconds: Long,
    val files: Array<Path>,
    val output: String,
    val packagemanagerSuccess: Boolean?,
    val packagemanagerExitValue: Int?,
    val packagemanagerDurationMilliseconds: Long?,
    val packagemanagerOutput: String?
) {
    constructor(
        latexCommandResult: CommandResult,
        files: Array<Path>,
        mpmCommandResult: CommandResult?
    ) : this(
        latexCommandResult.successful,
        latexCommandResult.exitValue,
        latexCommandResult.durationMilliseconds,
        files,
        latexCommandResult.output,
        mpmCommandResult?.successful,
        mpmCommandResult?.exitValue,
        mpmCommandResult?.durationMilliseconds,
        mpmCommandResult?.output
    )
}
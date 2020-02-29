package de.debuglevel.latex.packages

import de.debuglevel.latex.command.Command
import de.debuglevel.latex.command.CommandResult
import mu.KotlinLogging

object MiktexPackageManager {
    private val logger = KotlinLogging.logger {}

    private val installedPackages = mutableSetOf<String>()

    /**
     * Install the given packages if not already done.
     */
    fun install(latexPackages: Array<String>): CommandResult {
        val mpmCommandResults = latexPackages
            .map { install(it) }
            .filterNotNull()

        val mpmCommandResult = CommandResult(
            Command(
                mpmCommandResults.map { it.command.command }.joinToString("; ")
            ),
            mpmCommandResults.map { it.exitValue }.max() ?: 0,
            mpmCommandResults.map { it.durationMilliseconds }.sum(),
            mpmCommandResults.map { it.output }.joinToString("\n======\n")
        )

        return mpmCommandResult
    }

    /**
     * Install the given package if not already done.
     */
    fun install(latexPackage: String): CommandResult? {
        return if (installedPackages.contains(latexPackage)) {
            logger.debug { "Skipping installing package '$latexPackage' as it was already installed..." }
            null
        } else {
            logger.debug { "Installing package '$latexPackage'..." }
            val mpmCommandResult = Command("mpm --require=$latexPackage").run()

            // "--require" should also be successful if package is already installed
            if (mpmCommandResult.successful) {
                installedPackages.add(latexPackage)
            }

            logger.debug { "Installing package '$latexPackage' done" }
            mpmCommandResult
        }
    }
}
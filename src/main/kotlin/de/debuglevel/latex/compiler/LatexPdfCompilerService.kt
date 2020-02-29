package de.debuglevel.latex.compiler

import de.debuglevel.latex.command.Command
import de.debuglevel.latex.packages.MiktexPackageManager
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Singleton
import kotlin.streams.toList

/**
 * Compiles LaTeX to PDF
 */
@Singleton
class LatexPdfCompilerService(private val miktexPackageManager: MiktexPackageManager) {
    private val logger = KotlinLogging.logger {}
    private val outputDirectory = "output"

    /**
     * Compile the "main.tex" LaTeX file in the working directory to PDF.
     * The output will be placed in the directory specified in the 'outputDirectory' constant.
     */
    fun compile(workingDirectory: Path, requiredPackages: Array<String>): LatexPdfCompilerResult {
        return try {
            logger.debug { "Compiling LaTeX to PDF..." }

            val mpmCommandResult = miktexPackageManager.install(requiredPackages)

            val pdflatexCommandResult =
                Command(
                    "pdflatex -interaction=nonstopmode -output-directory=$outputDirectory main.tex",
                    workingDirectory
                ).run()

            val files = Files.walk(workingDirectory.resolve(outputDirectory))
                .filter { Files.isRegularFile(it) }
                .peek { logger.debug { "File found in output directory: '$it' (${it.toAbsolutePath()})" } }
                .toList()
                .toTypedArray()

            logger.debug { "${files.size} files in output directory" }

            val compilerResult = LatexPdfCompilerResult(
                pdflatexCommandResult,
                files,
                mpmCommandResult,
                workingDirectory
            )

            logger.debug { "Compiled LaTeX to PDF" }
            compilerResult
        } catch (e: Exception) {
            // this exception is NOT raised if the exit value is != 0
            logger.error(e) { "Compiling LaTeX to PDF failed" }
            throw CommandException("Compiling LaTeX to PDF failed")
        }
    }

    class CommandException(message: String) : Exception(message)
}
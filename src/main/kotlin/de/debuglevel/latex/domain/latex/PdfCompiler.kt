package de.debuglevel.latex.domain.latex

import de.debuglevel.latex.domain.command.Command
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

/**
 * Compiles LaTeX to PDF
 */
object PdfCompiler {
    private val logger = KotlinLogging.logger {}
    const val outputDirectory = "output"

    /**
     * Compile the "main.tex" LaTeX file in the working directory to PDF.
     * The output will be placed in the directory specified in the 'outputDirectory' constant.
     */
    fun compile(workingDirectory: Path): CompilerResult {
        return try {
            logger.debug { "Compiling LaTeX to PDF..." }

            val commandResult =
                Command(
                    "pdflatex -interaction=nonstopmode -output-directory=$outputDirectory main.tex",
                    workingDirectory
                ).run()

            val files = Files.walk(workingDirectory.resolve(outputDirectory))
                .filter { Files.isRegularFile(it) }
                .peek { logger.debug { "File found in output directory: '$it' (${it.toAbsolutePath()})" } }
                .toList()
                .toTypedArray()

            logger.debug { "Number of files in output directory: ${files.size}" }

            val compilerResult = CompilerResult(commandResult, files)

            logger.debug { "Compiling LaTeX to PDF done." }
            compilerResult
        } catch (e: Exception) {
            // this exception is NOT raised if the exit value is != 0
            logger.error(e) { "Compiling LaTeX to PDF failed" }
            throw CommandException("Compiling LaTeX to PDF failed")
        }
    }

    class CommandException(message: String) : Exception(message)
}
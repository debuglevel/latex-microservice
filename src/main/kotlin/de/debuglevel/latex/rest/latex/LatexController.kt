package de.debuglevel.latex.rest.latex

import com.google.gson.Gson
import de.debuglevel.latex.domain.latex.DocumentStorage
import de.debuglevel.latex.domain.latex.PdfCompiler
import de.debuglevel.latex.rest.responsetransformer.JsonTransformer
import mu.KotlinLogging
import spark.kotlin.RouteHandler
import java.util.*

object LatexController {
    private val logger = KotlinLogging.logger {}

    fun String.toBase64() = Base64.getEncoder().encodeToString(this.toByteArray())
    fun ByteArray.toBase64() = Base64.getEncoder().encodeToString(this)

    fun getOneJson(): RouteHandler.() -> Any {
        return {
            val id = params(":id")

            val storedFileTransfer = DocumentStorage.get(UUID.fromString(id))
            if (storedFileTransfer.path == null) {
                throw Exception("Path must not be null")
            }

            try {
                val workingDirectory = storedFileTransfer.path
                val compilerResult =
                    PdfCompiler.compile(workingDirectory, storedFileTransfer.requiredPackages ?: arrayOf())
                val fileTransferDto = LatexResponseDTO(
                    compilerResult.success,
                    compilerResult.exitValue,
                    compilerResult.durationMilliseconds,
                    compilerResult.files.map {
                        FileDTO(
                            workingDirectory.relativize(it).toString(),
                            it.toFile().readBytes().toBase64()
                        )
                    }.toTypedArray(),
                    compilerResult.output
                )

                type(contentType = "application/json")
                JsonTransformer.render(fileTransferDto)
            } catch (e: PdfCompiler.CommandException) {
                logger.info("Document '$id' could not be compiled: ", e.message)
                type("application/json")
                status(400)
                "{\"message\":\"document '$id' could not be compiled: ${e.message}\"}"
            }
        }
    }

    fun postOne(): RouteHandler.() -> Any {
        return {
            if (request.contentType() != "application/json") {
                logger.info("Content-Type ${request.contentType()} is not supported.")
                status(415)
                type("application/json")
                "{\"message\":\"${request.contentType()} is not supported.\"}"
            } else {
                val fileTransferDTO = Gson().fromJson(request.body(), LatexRequestDTO::class.java)

                val latexWithUUID = DocumentStorage.add(fileTransferDTO)
                status(201)
                type("text/plain")
                latexWithUUID.uuid
            }
        }
    }
}
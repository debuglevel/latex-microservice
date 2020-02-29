package de.debuglevel.latex

import de.debuglevel.latex.storage.DocumentStorage
import de.debuglevel.latex.compiler.LatexPdfCompilerResult
import de.debuglevel.latex.compiler.LatexPdfCompilerService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import mu.KotlinLogging
import java.util.*

@Controller("/documents")
class LatexController(private val latexService: LatexPdfCompilerService,
                      private val documentStorage: DocumentStorage) {
    private val logger = KotlinLogging.logger {}

    private fun compileDocument(uuid: UUID): LatexPdfCompilerResult {
        val storedFile = documentStorage.get(uuid)
        val workingDirectory = storedFile.path!!
        val compilerResult = latexService.compile(workingDirectory, storedFile.requiredPackages ?: arrayOf())
        return compilerResult
    }

    @Post("/")
    fun postOne(latexRequest: LatexRequest): HttpResponse<*> {
        logger.debug("Called postOne($latexRequest)")

        return try {
            val storedLatexFile = documentStorage.add(latexRequest)
            if (latexRequest.blocking) {
                val latexPdfCompilerResult = compileDocument(storedLatexFile.uuid)
                documentStorage.remove(storedLatexFile.uuid)
                HttpResponse.ok(LatexResponse(latexPdfCompilerResult))
            } else {
                HttpResponse.created(storedLatexFile.uuid)
            }
        } catch (e: LatexPdfCompilerService.CommandException) {
            HttpResponse.badRequest<LatexResponse>()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<Any>()
        }
    }

    @Get("/{uuid}")
    fun getOne(uuid: UUID): HttpResponse<LatexResponse> {
        logger.debug("Called getOne($uuid)")
        return try {
            val compilerResult = compileDocument(uuid)

            HttpResponse.ok(LatexResponse(compilerResult))
        } catch (e: LatexPdfCompilerService.CommandException) {
            HttpResponse.badRequest<LatexResponse>()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<LatexResponse>()
        }
    }

    @Delete("/{uuid}")
    fun deleteOne(uuid: UUID): HttpResponse<Boolean> {
        logger.debug("Called deleteOne($uuid)")

        return try {
            val documentDeleted = documentStorage.remove(uuid)
            HttpResponse.ok(documentDeleted)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<Boolean>()
        }
    }
}
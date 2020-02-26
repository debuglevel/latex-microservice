package de.debuglevel.latex.rest

import de.debuglevel.latex.rest.latex.LatexController
import de.debuglevel.microservices.utils.apiversion.apiVersion
import de.debuglevel.microservices.utils.logging.buildRequestLog
import de.debuglevel.microservices.utils.logging.buildResponseLog
import de.debuglevel.microservices.utils.spark.configuredPort
import de.debuglevel.microservices.utils.status.status
import mu.KotlinLogging
import spark.Spark.path
import spark.kotlin.*


/**
 * REST endpoint
 */
class RestEndpoint {
    private val logger = KotlinLogging.logger {}

    /**
     * Starts the REST endpoint to enter a listening state
     *
     * @param args parameters to be passed from main() command line
     */
    fun start(args: Array<String>) {
        logger.info("Starting...")
        configuredPort()
        status(this::class.java)

        apiVersion("1", true)
        {
            path("/documents") {
                post("/", function = LatexController.postOne())

                path("/:id") {
                    get("", "application/json", LatexController.getOneJson())
                    get("/", "application/json", LatexController.getOneJson())
                    delete("", "application/json", LatexController.deleteOne())
                    delete("/", "application/json", LatexController.deleteOne())
                }
            }
        }

        // add loggers
        before { logger.debug(buildRequestLog(request)) }
        after { logger.debug(buildResponseLog(request, response)) }
    }
}

package de.debuglevel.latex.command

/**
 * Result of the command
 *
 * @param command The command which was executed
 * @param exitValue Exit value/return code of the command
 * @param durationMilliseconds Time in milliseconds how long the command ran
 * @param output Stdout and stderr output of the command
 */
data class CommandResult(
    val command: Command,
    val exitValue: Int,
    val durationMilliseconds: Long,
    val output: String
) {
    /**
     * Whether the command was successful (i.e. exit value is 0)
     */
    val successful
        get() = exitValue == 0
}
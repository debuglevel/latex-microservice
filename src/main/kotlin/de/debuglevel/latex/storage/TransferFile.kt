package de.debuglevel.latex.storage

import com.fasterxml.jackson.annotation.JsonIgnore
import java.nio.charset.Charset
import java.util.*

data class TransferFile(
    val name: String,
    val base64data: String
) {
    @get:JsonIgnore
    val asString: String
        get() = asByteArray.toString(Charset.defaultCharset())

    @get:JsonIgnore
    val asByteArray: ByteArray
        get() = Base64.getDecoder().decode(base64data.toByteArray())
}
package de.debuglevel.latex.util

import java.util.*

fun String.toBase64() = Base64.getEncoder().encodeToString(this.toByteArray())
fun ByteArray.toBase64() = Base64.getEncoder().encodeToString(this)
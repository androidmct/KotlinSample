package com.bytepace.dimusco.utils

import okio.ByteString
import java.security.MessageDigest
import kotlin.experimental.and

private const val MD5 = "MD5"

fun getMd5(bytes: ByteString): String {
    val digest = MessageDigest.getInstance(MD5).digest(bytes.toByteArray())
    return StringBuilder().apply {
        for (byte in digest) {
            append("%02x".format(byte))
        }
    }.toString()
}

fun getSecurePassword(password: String): String {
    val digest = MessageDigest.getInstance(MD5).apply {
        update(password.toByteArray())
    }.digest()

    return StringBuilder().apply {
        for (byte in digest) {
            append(((byte and 0xff.toByte()) + 0x100).toString().substring(1))
        }
    }.toString()
}
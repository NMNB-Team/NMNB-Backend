package nmnb.r2dbc.user

import com.aventrix.jnanoid.jnanoid.NanoIdUtils

object CustomIdGenerator {
    private const val ID_LENGTH: Int = 16
    private const val CUSTOM_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun generateId(): String =
        NanoIdUtils.randomNanoId(
            NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
            CUSTOM_STRING.toCharArray(),
            ID_LENGTH,
        )
}

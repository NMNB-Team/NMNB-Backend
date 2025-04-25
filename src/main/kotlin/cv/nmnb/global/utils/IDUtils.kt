package cv.nmnb.global.utils

import com.aventrix.jnanoid.jnanoid.NanoIdUtils

object IDUtils {
    private const val ID_LENGTH: Int = 16
    private const val CUSTOM_ALPHABET_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#\$%^&*"
    fun customIdGenerator(): String {
        return NanoIdUtils.randomNanoId(
            NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
            CUSTOM_ALPHABET_STRING.toCharArray(),
            ID_LENGTH,
        )
    }
}

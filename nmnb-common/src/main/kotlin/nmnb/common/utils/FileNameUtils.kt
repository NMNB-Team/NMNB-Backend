package nmnb.common.utils

import com.aventrix.jnanoid.jnanoid.NanoIdUtils

object FileNameUtils {
    private const val ID_LENGTH: Int = 9
    private const val CUSTOM_STRING = "1234567890"

    fun generateFileName(date: String, name: String): String {
        return date + "_" + NanoIdUtils.randomNanoId(
            NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
            CUSTOM_STRING.toCharArray(),
            ID_LENGTH,
        ) + "-" + name
    }
}

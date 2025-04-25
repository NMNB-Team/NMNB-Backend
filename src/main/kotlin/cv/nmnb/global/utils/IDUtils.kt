package cv.nmnb.global.utils

import java.util.UUID

object IDUtils {
    private const val ID_LENGTH: Int = 16

    fun customIdGenerator(): String {
        val symbols = listOf('!', '@', '#', '$', '%', '&', '*')

        return UUID.randomUUID()
            .toString()
            .map { if (it == '-') symbols.random() else it }
            .joinToString("")
            .take(ID_LENGTH)
    }
}

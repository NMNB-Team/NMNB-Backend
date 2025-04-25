package cv.nmnb.global.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    fun formatDate(dateTime: LocalDateTime?): String {
        return dateTime?.format(formatter) ?: ""
    }
}

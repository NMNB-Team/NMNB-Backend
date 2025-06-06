package nmnb.common.utils

import org.springframework.stereotype.Component
@Component
class S3Utils() {
    fun generateS3Key(folder: String, baseFileName: String): String {
        return "$folder/$baseFileName"
    }
}

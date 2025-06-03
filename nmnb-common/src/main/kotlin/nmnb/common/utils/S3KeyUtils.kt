package nmnb.common.utils

object S3KeyUtils {
    fun generateS3Key(folder: String, baseFileName: String): String {
        return "$folder/$baseFileName"
    }
}

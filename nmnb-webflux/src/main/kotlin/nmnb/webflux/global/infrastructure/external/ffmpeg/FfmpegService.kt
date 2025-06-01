package nmnb.webflux.global.infrastructure.external.ffmpeg

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nmnb.common.response.exception.PostException
import nmnb.common.response.status.ErrorStatus
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Component
class FfmpegService {
    suspend fun createThumbnail(videoFile: File): File = withContext(Dispatchers.IO) {
        val outputFile = File.createTempFile("thumbnail_", ".jpg")
        val ffmpeg = ProcessBuilder(
            "ffmpeg",
            "-ss", "00:00:01",
            "-i", videoFile.absolutePath,
            "-frames:v", "1",
            "-vf", "thumbnail,scale=640:360:force_original_aspect_ratio=decrease,pad=640:360:-1:-1:ffffff,setsar=1",
            "-y",
            outputFile.absolutePath,
        )
        ffmpeg.redirectErrorStream(true)
        val process = ffmpeg.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val log = reader.readText()

        process.waitFor()

        if (process.exitValue() != 0) {
            println("FFmpeg failed:\n$log")
            outputFile.delete()
            throw PostException(ErrorStatus.POST_THUMBNAIL_GENERATION_FAILED)
        }
        outputFile
    }
}

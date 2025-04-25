package cv.nmnb.global.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class IDUtilsTest {
    @DisplayName("엔티티에 들어갈 id를 생성한다.")
    @Test
    fun customIdGenerator() {
        val result = IDUtils.customIdGenerator()

        assertEquals(16, result.length)
        assertFalse(result.contains("-"))
    }
}

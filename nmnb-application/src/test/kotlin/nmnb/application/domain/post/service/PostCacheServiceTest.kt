package nmnb.application.domain.post.service

import nmnb.application.IntegrationTestSupport
import nmnb.domain.post.repository.PostRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.CacheManager
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PostCacheServiceTest(
    @Autowired
    var postCacheService: PostCacheService,
    @Autowired
    var cacheManager: CacheManager,
) : IntegrationTestSupport() {
    @MockBean
    lateinit var postRepository: PostRepository

    @DisplayName("getAllPostIds 메서드는 캐시에 저장되며, 두 번째 호출부터는 캐시된 값이 반환된다")
    @Test
    fun getAllPostIdsWithCache() {
        // given
        val ids = listOf(1L)
        whenever(postRepository.findAllPostId()).thenReturn(ids)

        // when
        val firstResult = postCacheService.getAllPostIds()
        val secondResult = postCacheService.getAllPostIds()

        // then
        assertThat(firstResult).isEqualTo(ids)
        assertThat(firstResult).isEqualTo(ids)

        verify(postRepository, times(1)).findAllPostId()

        val cache = cacheManager.getCache("postIds")
        val cachedValue = cache?.get(SimpleKey.EMPTY)?.get()
        assertThat(cachedValue).isEqualTo(ids)
    }

    @DisplayName("Seed에 따라 결과를 고정하고, 캐시에 저장되어 두 번째 호출부터는 실제 메서드가 호출되지 않는다")
    @Test
    fun getShuffledIdsWithCache() {
        // given
        val ids = listOf(1L, 2L, 3L, 4L, 5L)
        val seed1 = 1234
        val seed2 = 5678

        // when
        val result1 = postCacheService.getShuffledIds(ids, seed1)
        val result2 = postCacheService.getShuffledIds(ids, seed1)
        val result3 = postCacheService.getShuffledIds(ids, seed2)

        // then
        assertThat(result1).isEqualTo(result2)
        assertThat(result1).isNotEqualTo(result3)
    }
}

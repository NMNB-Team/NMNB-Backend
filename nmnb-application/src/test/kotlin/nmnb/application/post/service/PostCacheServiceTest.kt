package nmnb.application.post.service

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
}

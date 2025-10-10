package nmnb.application.domain.badge.service

import nmnb.application.IntegrationTestSupport
import nmnb.domain.badge.Badge
import nmnb.domain.badge.UserBadge
import nmnb.domain.badge.repository.BadgeRepository
import nmnb.domain.badge.repository.UserBadgeRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
class BadgeServiceImplTest : IntegrationTestSupport() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var badgeRepository: BadgeRepository

    @Autowired
    private lateinit var userBadgeRepository: UserBadgeRepository

    @Autowired
    private lateinit var badgeService: BadgeService

    @AfterEach
    fun tearDown() {
        userBadgeRepository.deleteAllInBatch()
        badgeRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @DisplayName("사용자가 구매한 배지가 없는 경우 - 모든 배지가 구매되지 않은 상태로 반환")
    @Test
    fun getBadges() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        val badge1 = Badge.fixture("멍1 Badge", 100, true)
        val badge2 = Badge.fixture("멍2 Badge", 400, true)
        val badge3 = Badge.fixture("멍3 Badge", 300, true)
        badgeRepository.saveAll(listOf(badge1, badge2, badge3))

        // when
        val result = badgeService.getBadges(user.id!!)

        // then
        assertThat(result.badgeInfos).hasSize(3)
        assertThat(result.badgeInfos).allMatch { !it.isPurchased }
        assertThat(result.badgeInfos).allMatch { it.purchasedAt == null }

        val prices = result.badgeInfos.map { it.price }
        assertThat(prices).isEqualTo(listOf(100, 300, 400))
    }

    @DisplayName("사용자가 일부 배지를 구매한 경우 - 구매한 배지는 구매 상태로 반환")
    @Test
    fun getBadgesWithSomePurchased() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        val badge1 = Badge.fixture("멍1 Badge", 100, true)
        val badge2 = Badge.fixture("멍2 Badge", 300, true)
        val badge3 = Badge.fixture("멍3 Badge", 400, true)
        badgeRepository.saveAll(listOf(badge1, badge2, badge3))

        userBadgeRepository.saveAll(
            listOf(
                UserBadge(user, badge1, LocalDateTime.now().minusDays(1)),
                UserBadge(user, badge3, LocalDateTime.now().minusHours(5)),
            ),
        )

        // when
        val result = badgeService.getBadges(user.id!!)

        // then
        assertThat(result.badgeInfos).hasSize(3)

        val firstBadge = result.badgeInfos[0]
        assertThat(firstBadge.name).isEqualTo("멍1 Badge")
        assertThat(firstBadge.price).isEqualTo(100)
        assertThat(firstBadge.isPurchased).isTrue
        assertThat(firstBadge.purchasedAt).isNotNull

        val secondBadge = result.badgeInfos[1]
        assertThat(secondBadge.name).isEqualTo("멍2 Badge")
        assertThat(secondBadge.price).isEqualTo(300)
        assertThat(secondBadge.isPurchased).isFalse
        assertThat(secondBadge.purchasedAt).isNull()

        val thirdBadge = result.badgeInfos[2]
        assertThat(thirdBadge.name).isEqualTo("멍3 Badge")
        assertThat(thirdBadge.price).isEqualTo(400)
        assertThat(thirdBadge.isPurchased).isTrue
        assertThat(thirdBadge.purchasedAt).isNotNull
    }

    @DisplayName("활성화된 배지가 없는 경우 - 빈 리스트 반환")
    @Test
    fun getBadgesWhenNoActiveBadges() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        // when
        val result = badgeService.getBadges(user.id!!)

        // then
        assertThat(result.badgeInfos).isEmpty()
    }
}

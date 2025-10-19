package nmnb.application.domain.badge.service

import nmnb.application.domain.badge.controller.dto.response.BadgeInfoListResponse
import nmnb.application.domain.badge.controller.dto.response.BadgeInfoResponse
import nmnb.common.response.exception.BadgeException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.badge.Badge
import nmnb.domain.badge.UserBadge
import nmnb.domain.badge.repository.BadgeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class BadgeServiceImpl(
    private val badgeRepository: BadgeRepository,
    private val userBadgeService: UserBadgeService,
) : BadgeService {
    override fun getBadges(userId: String): BadgeInfoListResponse {
        val badges = badgeRepository.findByActiveIsTrueOrderByPriceAsc()
        // 사용자가 구매한 배지들을 Map으로 변환 (badgeId -> UserBadge)
        val userBadges = userBadgeService.findByUserId(userId)
            .associateBy { it.badge.id }

        return createBadgeInfoListResponse(badges, userBadges)
    }

    override fun getBadge(userId: String, badgeId: Long): BadgeInfoResponse {
        val badge = badgeRepository.findById(badgeId)
            .orElseThrow { BadgeException(ErrorStatus.BADGE_NOTFOUND) }

        val userBadges = userBadgeService.findByUserId(userId)
            .associateBy { it.badge.id }

        return createBadgeInfoResponse(badge, userBadges)
    }

    private fun createBadgeInfoResponse(
        badge: Badge,
        userBadges: Map<Long?, UserBadge>,
    ): BadgeInfoResponse {
        val userBadge = userBadges[badge.id]

        return BadgeInfoResponse.of(
            badge = badge,
            isPurchased = userBadge != null,
            purchasedAt = userBadge?.purchasedAt,
        )
    }

    private fun createBadgeInfoListResponse(
        badges: List<Badge>,
        userBadges: Map<Long?, UserBadge>,
    ): BadgeInfoListResponse {
        val badgeResponses = createBadgeResponses(badges, userBadges)
        return BadgeInfoListResponse(badgeResponses)
    }

    private fun createBadgeResponses(
        badges: List<Badge>,
        userBadges: Map<Long?, UserBadge>,
    ) = badges.map { badge ->
        createBadgeInfoResponse(badge, userBadges)
    }
}

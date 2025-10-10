package nmnb.application.domain.badge.service

import nmnb.application.domain.badge.controller.dto.response.BadgeInfoListResponse
import nmnb.application.domain.badge.controller.dto.response.BadgeInfoResponse

interface BadgeService {
    fun getBadges(userId: String): BadgeInfoListResponse
    fun getBadge(userId: String, badgeId: Long): BadgeInfoResponse
}

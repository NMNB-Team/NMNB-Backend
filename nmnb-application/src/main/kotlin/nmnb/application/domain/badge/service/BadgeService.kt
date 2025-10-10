package nmnb.application.domain.badge.service

import nmnb.application.domain.badge.controller.dto.response.BadgeInfoListResponse

interface BadgeService {
    fun getBadges(userId: String): BadgeInfoListResponse
}

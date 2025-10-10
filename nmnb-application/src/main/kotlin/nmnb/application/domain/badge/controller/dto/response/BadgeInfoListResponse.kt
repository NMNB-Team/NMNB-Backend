package nmnb.application.domain.badge.controller.dto.response

import nmnb.common.utils.DateTimeUtils
import nmnb.domain.badge.Badge
import java.time.LocalDateTime

data class BadgeInfoListResponse(
    val badgeInfos: List<BadgeInfoResponse>,
)

data class BadgeInfoResponse(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean,
    val purchasedAt: String? = null,
) {
    companion object {
        fun of(badge: Badge, isPurchased: Boolean, purchasedAt: LocalDateTime? = null): BadgeInfoResponse {
            return BadgeInfoResponse(
                id = badge.id!!,
                name = badge.name,
                description = badge.description,
                imageUrl = badge.imageUrl,
                price = badge.price,
                isPurchased = isPurchased,
                purchasedAt = purchasedAt?.let { DateTimeUtils.formatDate(it) },
            )
        }
    }
}

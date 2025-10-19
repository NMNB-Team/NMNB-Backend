package nmnb.domain.badge.repository

import nmnb.domain.badge.UserBadge
import org.springframework.data.jpa.repository.JpaRepository

interface UserBadgeRepository : JpaRepository<UserBadge, Long> {
    fun findByUserId(userId: String): List<UserBadge>
}

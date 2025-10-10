package nmnb.domain.badge.repository

import nmnb.domain.badge.Badge
import org.springframework.data.jpa.repository.JpaRepository

interface BadgeRepository : JpaRepository<Badge, Long> {
    fun findByActiveIsTrueOrderByPriceAsc(): List<Badge>
}

package nmnb.application.domain.badge.service

import nmnb.domain.badge.UserBadge
import nmnb.domain.badge.repository.UserBadgeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class UserBadgeService(
    private val userBadgeRepository: UserBadgeRepository,
) {
    fun findByUserId(userId: String): List<UserBadge> =
        userBadgeRepository.findByUserId(userId)
}

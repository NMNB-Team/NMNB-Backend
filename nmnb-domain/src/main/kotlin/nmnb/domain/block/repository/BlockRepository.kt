package nmnb.domain.block.repository

import io.lettuce.core.dynamic.annotation.Param
import nmnb.domain.block.Block
import nmnb.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BlockRepository : JpaRepository<Block, Long> {
    @Query("SELECT b.blockedUser.id FROM Block b WHERE b.blocker.id=:userId")
    fun findByBlockerId(@Param("userId") userId: String): List<String>
    fun findByBlockerAndBlockedUser(blocker: User, blockerUser: User): Block?
}

package nmnb.domain.block

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nmnb.domain.JpaBaseEntity
import nmnb.domain.user.User

@Entity
@Table(
    name = "blocks",
    uniqueConstraints = [UniqueConstraint(columnNames = ["blocker_id", "blocked_id"])],
)
class Block(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    val blocker: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    val blockedUser: User,
) : JpaBaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    companion object {
        fun fixture(
            blocker: User,
            blockedUser: User,
        ): Block {
            return Block(blocker, blockedUser)
        }
    }
}

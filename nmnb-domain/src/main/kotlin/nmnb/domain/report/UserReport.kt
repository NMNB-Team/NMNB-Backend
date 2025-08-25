package nmnb.domain.report

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import nmnb.domain.user.User

@Entity
@DiscriminatorValue("USER")
class UserReport(
    @Column(nullable = true)
    val targetUserId: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false, insertable = false, updatable = false)
    override val reporter: User,
    override val content: ContentType,
) : Report(reporter = reporter, content = content) {
    companion object {
        fun fixture(
            targetUserId: String,
            reporter: User,
            content: ContentType = ContentType.SEXUAL,
        ): UserReport {
            return UserReport(targetUserId, reporter, content)
        }
    }
}

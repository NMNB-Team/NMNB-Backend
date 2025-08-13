package nmnb.domain.report

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import nmnb.domain.user.User

@Entity
@DiscriminatorValue("POST")
class PostReport(
    @Column(nullable = true)
    val targetPostId: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false, insertable = false, updatable = false)
    override val reporter: User,
    override val content: ContentType,
) : Report(reporter = reporter, content = content) {
    companion object {
        fun fixture(
            targetId: Long,
            reporter: User,
            content: ContentType = ContentType.SEXUAL,
        ): PostReport {
            return PostReport(targetId, reporter, content)
        }
    }
}

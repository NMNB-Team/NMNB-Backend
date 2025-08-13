package nmnb.domain.report

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("POST")
class PostReport(
    @Column(nullable = true)
    val postId: Long? = null,
    override val reporterId: String,
    override val content: ContentType,
) : Report(reporterId = reporterId, content = content)

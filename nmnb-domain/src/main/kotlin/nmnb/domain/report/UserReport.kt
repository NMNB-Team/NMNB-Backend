package nmnb.domain.report

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("USER")
class UserReport(
    @Column(nullable = false)
    val userId: String,
    override val reporterId: String,
    override val content: ContentType,
) : Report(reporterId = reporterId, content = content)

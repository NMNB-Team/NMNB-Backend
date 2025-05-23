package nmnb.r2dbc

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
abstract class R2dbcBaseEntity {
    @CreatedDate
    @Column("created_at")
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column("modified_at")
    var modifiedAt: LocalDateTime? = null
}

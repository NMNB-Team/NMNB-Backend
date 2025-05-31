package nmnb.r2dbc.post

import nmnb.r2dbc.R2dbcBaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("posts")
data class R2dbcPost(
    @Column("url")
    val url: String,

    @Column("thumbnail_url")
    val thumbnailUrl: String? = null,

    @Column("description")
    var description: String? = null,

    @Column("user_id")
    val userId: String? = null,

    @Id
    @Column("post_id")
    val id: Long? = null,
) : R2dbcBaseEntity() {

    fun updateThumbnail(newThumbnailUrl: String): R2dbcPost {
        return R2dbcPost(
            url = this.url,
            thumbnailUrl = newThumbnailUrl,
            description = this.description,
            userId = this.userId,
            id = this.id,
        ).also { updated ->
            updated.createdAt = this.createdAt
            updated.modifiedAt = this.modifiedAt
        }
    }

    companion object {
        fun fixture(
            url: String = "url",
            thumbnailUrl: String = "thumbnail",
            description: String? = null,
            userId: String? = null,
            id: Long? = null,
        ): R2dbcPost {
            return R2dbcPost(url, thumbnailUrl, description, userId, id)
        }
    }
}

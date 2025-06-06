package nmnb.domain.post

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import nmnb.domain.JpaBaseEntity
import nmnb.domain.user.User

@Entity
@Table(name = "posts")
class Post(
    @Column(nullable = false)
    val url: String,

    @Column(nullable = false, length = 1000)
    val thumbnailUrl: String,

    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : JpaBaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    val id: Long? = null

    companion object {
        fun fixture(
            url: String = "url",
            thumbnailUrl: String = "thumbnail",
            description: String? = null,
            user: User = User.fixture(),
        ): Post {
            return Post(url, thumbnailUrl, description, user)
        }
    }
}

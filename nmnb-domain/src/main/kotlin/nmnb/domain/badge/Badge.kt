package nmnb.domain.badge

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import nmnb.domain.JpaBaseEntity

@Entity
@Table(name = "badges")
class Badge(
    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val active: Boolean = true,
) : JpaBaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    val id: Long? = null

    companion object {
        fun fixture(
            name: String = "Test Badge",
            price: Int = 100,
            active: Boolean = true,
            description: String = "test badge",
            imageUrl: String = "test.png",
        ): Badge {
            return Badge(
                name = name,
                description = description,
                price = price,
                imageUrl = imageUrl,
                active = active,
            )
        }
    }
}

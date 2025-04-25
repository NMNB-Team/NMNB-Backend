package cv.nmnb.domain.user.domain

import cv.nmnb.domain.post.domain.Post
import cv.nmnb.global.common.domain.BaseEntity
import cv.nmnb.global.utils.IDUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var profileImage: String,

    var companionAnimal: String? = null,
) : BaseEntity() {
    @Id
    @Column(name = "user_id")
    val id: String = IDUtils.customIdGenerator()

    @Enumerated(EnumType.STRING)
    var petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var posts: MutableList<Post> = mutableListOf()
}

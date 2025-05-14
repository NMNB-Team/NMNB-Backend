package nmnb.domain.user

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import nmnb.common.domain.PetOwnershipStatus
import nmnb.domain.JpaBaseEntity
import nmnb.domain.post.Post
import nmnb.domain.user.generator.annotation.UserId

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var profileImage: String,

    var petName: String? = null,
) : JpaBaseEntity() {
    @Id
    @field:UserId
    @Column(name = "user_id")
    var id: String? = null

    @Enumerated(EnumType.STRING)
    var petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var posts: MutableList<Post> = mutableListOf()

    val nickName: String
        get() = petName?.let { petName -> "$petName-$id" } ?: id.toString()
    val hasAnimal: Boolean
        get() = petOwnershipStatus == PetOwnershipStatus.HAS_PET

    companion object {

        fun fixture(
            id: String? = null,
            email: String = "ex@example.com",
            profileImage: String = "default.png",
            petName: String? = null,
            petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET,
            posts: MutableList<Post> = mutableListOf(),
        ): User {
            val user = User(
                email = email,
                profileImage = profileImage,
                petName = petName,
            )
            user.id = id
            user.petOwnershipStatus = petOwnershipStatus
            user.posts = posts
            return user
        }
    }

    fun updatePetName(petName: String) {
        this.petName = petName
    }

    fun updatePetOwnershipStatus(status: PetOwnershipStatus) {
        this.petOwnershipStatus = status
    }
}

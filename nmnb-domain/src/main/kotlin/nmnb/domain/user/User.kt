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
import nmnb.common.domain.SignUpStatus
import nmnb.domain.JpaBaseEntity
import nmnb.domain.post.Post
import nmnb.domain.user.generator.annotation.UserId
import java.util.UUID

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
    var petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.UNKNOWN

    @Enumerated(EnumType.STRING)
    var signUpStatus: SignUpStatus = SignUpStatus.IN_PROGRESS

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var posts: MutableList<Post> = mutableListOf()

    val nickName: String
        get() = petName?.let { petName -> "$petName-$id" } ?: id.toString()

    companion object {

        fun fixture(
            id: String? = null,
            email: String = "${UUID.randomUUID()}@example.com",
            profileImage: String = "default.png",
            petName: String? = null,
            petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET,
            signUpStatus: SignUpStatus = SignUpStatus.COMPLETE,
            posts: MutableList<Post> = mutableListOf(),
        ): User {
            val user = User(
                email = email,
                profileImage = profileImage,
                petName = petName,
            )
            user.id = id
            user.petOwnershipStatus = petOwnershipStatus
            user.signUpStatus = signUpStatus
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

    fun updateSignUpStatus(status: SignUpStatus) {
        this.signUpStatus = status
    }
}

package nmnb.r2dbc.user

import nmnb.common.domain.PetOwnershipStatus
import nmnb.common.domain.SignUpStatus
import nmnb.r2dbc.R2dbcBaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
data class R2dbcUser(
    @Id
    @Column("user_id")
    val id: String? = null,

    @Column("email")
    val email: String,

    @Column("profile_image")
    var profileImage: String,

    @Column("pet_name")
    var petName: String? = null,

    @Column("pet_ownership_status")
    var petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET,

    @Column("sign_up_status")
    var signUpStatus: SignUpStatus = SignUpStatus.IN_PROGRESS,
) : R2dbcBaseEntity() {
    companion object {
        fun fixture(
            email: String = "${UUID.randomUUID()}@example.com",
            profileImage: String = "default.png",
            petName: String? = null,
            petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET,
            signUpStatus: SignUpStatus = SignUpStatus.COMPLETE,
        ): R2dbcUser {
            val user = R2dbcUser(
                email = email,
                profileImage = profileImage,
                petName = petName,
            )
            user.petOwnershipStatus = petOwnershipStatus
            user.signUpStatus = signUpStatus
            return user
        }
    }
}

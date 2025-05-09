package nmnb.r2dbc.user

import nmnb.common.domain.PetOwnershipStatus
import nmnb.r2dbc.R2dbcBaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class R2dbcUser(
    @Id
    @Column("user_id")
    val id: String? = null,

    @Column("email")
    val email: String,

    @Column("profile_image")
    var profileImage: String,

    @Column("companion_animal")
    var companionAnimal: String? = null,

    @Column("pet_ownership_status")
    var petOwnershipStatus: PetOwnershipStatus = PetOwnershipStatus.NO_PET,
) : R2dbcBaseEntity()

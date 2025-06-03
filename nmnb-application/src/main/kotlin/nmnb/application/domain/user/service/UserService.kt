package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.request.EditProfileServiceRequest
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationServiceRequest
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.domain.user.User
import org.springframework.web.multipart.MultipartFile

interface UserService {
    fun getProfile(user: User): UserProfileResponse
    fun registerWithPetName(user: User, request: UserPetRegistrationServiceRequest): UserStatusResponse
    fun registerWithoutPet(user: User): UserStatusResponse
    fun editProfile(user: User, request: EditProfileServiceRequest, profileImage: MultipartFile? = null)
}

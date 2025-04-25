package cv.nmnb.domain.user.service

import cv.nmnb.domain.user.domain.User
import cv.nmnb.domain.user.service.dto.response.UserProfileResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserServiceImpl : UserService {

    override fun getProfile(user: User): UserProfileResponse {
        return UserProfileResponse.of(user)
    }
}

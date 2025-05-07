package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserServiceImpl : UserService {

    override fun getProfile(user: User): UserProfileResponse {
        return UserProfileResponse.of(user)
    }
}

package nmnb.application.domain.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.user.service.UserService
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationRequest
import nmnb.application.domain.user.service.dto.response.UserPetStatusResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "User 👤", description = "사용자 관련 API")
@RequestMapping("/v1/api/users")
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "사용자 마이페이지 조회 API", description = "사용자의 마이페이지를 조회합니다._숙희")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @GetMapping("/profile")
    fun getProfile(@RequestBody user: User): BaseResponse<UserProfileResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, userService.getProfile(user))
    }

    @Operation(summary = "반려견 이름 등록 API", description = "가입하는 유저가 반려견의 이름을 등록합니다. 유저의 petOwnershipStatus가 `HAS_PET`으로 설정됩니다._예림")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @PatchMapping("/pet")
    fun setPetOwnershipWithName(@RequestBody request: UserPetRegistrationRequest): BaseResponse<UserPetStatusResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, userService.registerWithPet(User.fixture("AuthUser 구현 후 수정"), request.petName))
    }
}

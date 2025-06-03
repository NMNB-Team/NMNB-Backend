package nmnb.application.domain.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.user.controller.dto.request.EditProfileRequest
import nmnb.application.domain.user.controller.dto.request.UserPetRegistrationRequest
import nmnb.application.domain.user.service.UserService
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.application.global.auth.generator.annotation.AuthUser
import nmnb.application.global.auth.generator.annotation.TokenApiResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "User 👤", description = "사용자 관련 API")
@RequestMapping("/v1/api/users")
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "사용자 마이페이지 조회 API", description = "사용자의 마이페이지를 조회합니다._숙희")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @TokenApiResponse
    @GetMapping("/profile")
    fun getProfile(@Parameter(name = "user", hidden = true) @AuthUser user: User): BaseResponse<UserProfileResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, userService.getProfile(user))
    }

    @Operation(summary = "사용자 마이페이지 수정 API", description = "사용자의 마이페이지를 수정합니다._숙희")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
        ApiResponse(responseCode = "PET400", description = "반려동물 이름은 필수입니다."),
        ApiResponse(responseCode = "PET401", description = "반려동물 등록이 필요합니다."),
        ApiResponse(responseCode = "PET402", description = "반려동물 상태 값이 유효하지 않습니다."),
        ApiResponse(responseCode = "S3501", description = "프로필 이미지 업로드 중 오류가 발생했습니다."),
    )
    @PostMapping("/profile", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun editProfile(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
        @RequestPart(value = "request") @Parameter(
            description = """다음과 같은 JSON 형태의 요청을 입력해야 합니다:
                {
                    "petName": "petName"
                }
                """,
            schema = Schema(
                type = "string",
                format = "binary",
            ),
        ) request: EditProfileRequest,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?,
    ): BaseResponse<Any> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            userService.editProfile(user, request.toServiceRequest(), profileImage),
        )
    }

    @Operation(
        summary = "반려견 이름 등록 API",
        description = "가입하는 유저가 반려견의 이름을 등록합니다. 유저의 petOwnershipStatus가 `HAS_PET`으로 설정됩니다._예림",
    )
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @TokenApiResponse
    @PatchMapping("/pet")
    fun registerWithPetName(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
        @RequestBody request: UserPetRegistrationRequest,
    ): BaseResponse<UserStatusResponse> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            userService.registerWithPetName(user, request.toServiceRequest()),
        )
    }

    @Operation(
        summary = "반려견 미보유 상태 설정 API",
        description = "가입하는 유저가 반려견을 보유하지 않았음을 설정합니다. petOwnershipStatus가 NO_PET으로 설정됩니다._예림",
    )
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @PatchMapping("/pet/none")
    fun registerWithoutPet(
        @Parameter(
            name = "user",
            hidden = true,
        ) @AuthUser user: User,
    ): BaseResponse<UserStatusResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, userService.registerWithoutPet(user))
    }
}

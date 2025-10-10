package nmnb.application.domain.badge.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.badge.controller.dto.response.BadgeInfoListResponse
import nmnb.application.domain.badge.controller.dto.response.BadgeInfoResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.kotlin.eq
import nmnb.common.response.exception.BadgeException
import nmnb.common.response.status.ErrorStatus

class BadgeControllerTest : ControllerTestSupport() {

    @WithMockUser
    @DisplayName("뱃지 조회에 성공한다")
    @Test
    fun getBadges() {
        // given
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)

        val badgeInfoListResponse = BadgeInfoListResponse(
            badgeInfos = listOf(
                BadgeInfoResponse(
                    id = 1L,
                    name = "멍1 Badge",
                    description = "멍멍이1 배지",
                    imageUrl = "mong.png",
                    price = 100,
                    isPurchased = false,
                    purchasedAt = null,
                ),
            ),
        )

        whenever(badgeService.getBadges(any())).thenReturn(badgeInfoListResponse)

        // when & then
        mockMvc.perform(
            get("/v1/api/badge")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.badgeInfos").isArray)
            .andExpect(jsonPath("$.result.badgeInfos[0].id").value(1))
            .andExpect(jsonPath("$.result.badgeInfos[0].name").value("멍1 Badge"))
    }

    @WithMockUser
    @DisplayName("뱃지가 없는 경우 빈 리스트를 반환한다")
    @Test
    fun getBadgesWhenEmpty() {
        // given
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)

        val emptyBadgeInfoListResponse = BadgeInfoListResponse(badgeInfos = emptyList())

        whenever(badgeService.getBadges(any()))
            .thenReturn(emptyBadgeInfoListResponse)

        // when & then
        mockMvc.perform(
            get("/v1/api/badge")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.badgeInfos").isArray)
            .andExpect(jsonPath("$.result.badgeInfos").isEmpty)
    }

    @WithMockUser
    @DisplayName("특정 뱃지 단건 조회에 성공한다")
    @Test
    fun getBadge() {
        // given
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)

        val badgeId = 1L
        val response = BadgeInfoResponse(
            id = 1L,
            name = "멍1 Badge",
            description = "멍멍이1 배지",
            imageUrl = "mong.png",
            price = 100,
            isPurchased = false,
            purchasedAt = null,
        )

        whenever(badgeService.getBadge(eq(user.id!!), eq(badgeId))).thenReturn(response)

        // when & then
        mockMvc.perform(
            get("/v1/api/badge/$badgeId")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.id").value(badgeId))
    }

    @WithMockUser
    @DisplayName("존재하지 않는 뱃지 조회시 404를 반환한다")
    @Test
    fun getBadge_notFound() {
        // given
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()
        mockUserAuthentication(user, accessToken, deviceId)

        val badgeId = 404L
        whenever(badgeService.getBadge(eq(user.id!!), eq(badgeId))).thenThrow(BadgeException(ErrorStatus.BADGE_NOTFOUND))

        // when & then
        mockMvc.perform(
            get("/v1/api/badge/$badgeId")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value(ErrorStatus.BADGE_NOTFOUND.code))
    }
}

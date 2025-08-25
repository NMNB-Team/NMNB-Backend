package nmnb.webflux.global.auth.service

import nmnb.common.auth.RefreshToken
import nmnb.common.auth.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Transactional
    fun upsertRefreshToken(email: String, deviceId: String, refreshToken: String): Mono<Void> {
        val tokenId = "$email:$deviceId"
        val now = LocalDateTime.now()

        return Mono.fromCallable {
            val existing = refreshTokenRepository.findById(tokenId).orElse(null)

            if (existing != null) {
                existing.update(refreshToken, now)
                refreshTokenRepository.save(existing)
            } else {
                refreshTokenRepository.save(
                    RefreshToken(
                        id = tokenId,
                        email = email,
                        refreshToken = refreshToken,
                        deviceId = deviceId,
                        timeStamp = now,
                    ),
                )
            }
        }.subscribeOn(Schedulers.boundedElastic()).then()
    }

    @Transactional
    fun removeOldestTokenIfLimitExceeded(email: String): Mono<Void> {
        return Mono.fromCallable {
            refreshTokenRepository.findAll()
                .filter { it.email == email }
                .sortedBy { it.timeStamp }
                .let { allTokens ->
                    if (allTokens.size <= MAX_REFRESH_TOKENS) return@fromCallable
                    val tokensToRemove = allTokens.dropLast(MAX_REFRESH_TOKENS)

                    tokensToRemove.forEach { token ->
                        refreshTokenRepository.deleteById(token.id)
                    }
                }
        }.subscribeOn(Schedulers.boundedElastic()).then()
    }

    companion object {
        const val MAX_REFRESH_TOKENS = 4
    }
}

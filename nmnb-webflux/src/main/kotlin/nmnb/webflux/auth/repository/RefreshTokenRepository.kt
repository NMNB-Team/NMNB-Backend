package nmnb.webflux.auth.repository

import nmnb.webflux.auth.domain.RefreshToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, String>

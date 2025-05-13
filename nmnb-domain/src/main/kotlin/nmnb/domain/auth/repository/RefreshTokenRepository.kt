package nmnb.domain.auth.repository

import nmnb.domain.auth.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String>

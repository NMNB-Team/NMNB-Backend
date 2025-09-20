package nmnb.common.auth.repository

import nmnb.common.auth.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String>

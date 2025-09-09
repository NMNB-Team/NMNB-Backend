package nmnb.r2dbc.user

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface R2dbcUserRepository : ReactiveCrudRepository<R2dbcUser, String> {
    fun findByEmail(email: String): Mono<R2dbcUser>
}

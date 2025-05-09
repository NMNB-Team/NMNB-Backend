package nmnb.r2dbc.user

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface R2dbcUserRepository : ReactiveCrudRepository<R2dbcUser, Long> {
    fun findByEmail(name: String): Mono<R2dbcUser>
}

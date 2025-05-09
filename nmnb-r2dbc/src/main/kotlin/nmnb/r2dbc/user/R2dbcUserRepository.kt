package nmnb.r2dbc.user

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface R2dbcUserRepository : ReactiveCrudRepository<R2dbcUser, Long>

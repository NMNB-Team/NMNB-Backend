package nmnb.r2dbc.post

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface R2dbcPostRepository : ReactiveCrudRepository<R2dbcPost, Long>

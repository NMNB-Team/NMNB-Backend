package nmnb.domain.like.repository

import nmnb.domain.like.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long>, LikeRepositoryCustom

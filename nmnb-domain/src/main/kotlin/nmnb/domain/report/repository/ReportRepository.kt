package nmnb.domain.report.repository

import io.lettuce.core.dynamic.annotation.Param
import nmnb.domain.report.Report
import nmnb.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {
    @Query(
        "SELECT CASE WHEN COUNT(r) >0 THEN true ELSE false END FROM " +
            "PostReport r WHERE r.reporter=:reporter AND r.targetPostId=:targetId",
    )
    fun existsPostReport(@Param("reporter") reporter: User, @Param("targetId") targetId: Long): Boolean

    @Query(
        "SELECT CASE WHEN COUNT(r) >0 THEN true ELSE false END FROM " +
            "UserReport r WHERE r.reporter=:reporter AND r.targetUserId=:targetId",
    )
    fun existsUserReport(@Param("reporter") reporter: User, @Param("targetId") targetId: String): Boolean
}

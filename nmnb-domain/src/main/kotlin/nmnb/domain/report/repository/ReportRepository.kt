package nmnb.domain.report.repository

import io.lettuce.core.dynamic.annotation.Param
import nmnb.domain.report.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {
    @Query(
        "SELECT CASE WHEN COUNT(r) >0 THEN true ELSE false END FROM " +
            "PostReport r WHERE r.reporterId=:reporterId AND r.postId=:targetId",
    )
    fun existsPostReport(@Param("reporterId") reporterId: String, @Param("targetId") targetId: Long): Boolean
}

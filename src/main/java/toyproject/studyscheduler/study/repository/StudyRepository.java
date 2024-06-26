package toyproject.studyscheduler.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import toyproject.studyscheduler.study.domain.entity.Study;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select s from Study s where s.member.id = :memberId " +
        "and (s.studyPeriod.startDate <= :endDate)" +
        "and (s.studyPeriod.endDate >= :startDate) ")
    List<Study> findAllByPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("memberId") long memberId);
}

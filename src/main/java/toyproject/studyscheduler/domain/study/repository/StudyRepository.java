package toyproject.studyscheduler.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import toyproject.studyscheduler.domain.study.Study;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    // TODO : expectedEndDate -> realEndDate로 변경하기
    @Query("select s from Study s where (s.startDate >= :startDate and s.startDate <= :endDate)" +
        "or (s.expectedEndDate >= :startDate and s.expectedEndDate <= :endDate) ")
    List<Study> findAllByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

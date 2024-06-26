package toyproject.studyscheduler.dailystudy.repository;

import toyproject.studyscheduler.dailystudy.domain.entity.DailyStudy;
import toyproject.studyscheduler.common.domain.Period;

import java.util.List;

public interface DailyStudyQuerydslRepository {

    List<DailyStudy> findDailyStudyByConditions(Long studyId, Period period);
}

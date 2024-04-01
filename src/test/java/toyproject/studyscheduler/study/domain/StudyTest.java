package toyproject.studyscheduler.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toyproject.studyscheduler.member.domain.entity.Member;
import toyproject.studyscheduler.study.domain.entity.Lecture;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class StudyTest {

//    @DisplayName("학습을 완료한다.")
//    @Test
//    void terminateStudy() {
//        // given
//        LocalDate startDate = LocalDate.of(2023, 10, 1);
//        Member member = createMember();
//
//        int totalExpectedPeriod = 10;
//        Lecture lecture = createLecture(startDate, totalExpectedPeriod, member);
//
//        // when, then
//        assertThat(lecture.isTermination()).isFalse();
//        assertThat(lecture).extracting("realEndDate", "expectedEndDate")
//            .contains(LocalDate.EPOCH, LocalDate.of(2023, 10, 10));
//
//        LocalDate realEndDate = LocalDate.of(2023, 10, 21);
//        lecture.terminateStudyIn(realEndDate);
//
//        assertThat(lecture.isTermination()).isTrue();
//        assertThat(lecture).extracting("realEndDate", "expectedEndDate")
//            .contains(LocalDate.of(2023, 10, 21), LocalDate.of(2023, 10, 10));
//    }

    private static Member createMember() {
        return Member.builder()
            .email("hong@gmail.com")
            .password("zxcv1234")
            .name("hong")
            .build();
    }

    private static Lecture createLecture(LocalDate startDate, int totalExpectedPeriod, Member member) {
        return null;
//        return Lecture.builder()
//            .title("김영한의 스프링")
//            .description("스프링 핵심 강의")
//            .teacherName("김영한")
//            .planTimeInWeekday(30)
//            .planTimeInWeekend(100)
//            .totalExpectedPeriod(totalExpectedPeriod)
//            .startDate(startDate)
//            .member(member)
//            .totalRuntime(600)
//            .build();
    }
}
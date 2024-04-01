package toyproject.studyscheduler.study.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import toyproject.studyscheduler.member.domain.entity.Member;
import toyproject.studyscheduler.study.domain.StudyInformation;
import toyproject.studyscheduler.study.domain.StudyPeriod;

@DiscriminatorValue("lecture")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Lecture extends Study {

    private String teacherName;

    private int totalRuntime;

    @Builder
    private Lecture(StudyInformation studyInformation, StudyPeriod studyPeriod, Member member, String teacherName, int totalRuntime) {
        super(studyInformation, studyPeriod, member);
        this.teacherName = teacherName;
        this.totalRuntime = totalRuntime;
    }
}
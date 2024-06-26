package toyproject.studyscheduler.member.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import toyproject.studyscheduler.common.config.querydsl.QuerydslConfig;
import toyproject.studyscheduler.member.domain.entity.Member;
import toyproject.studyscheduler.member.exception.MemberException;

import static org.assertj.core.api.Assertions.*;

@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("아이디 값을 전달 받아 사용자의 정보를 조회한다.")
    @Test
    void findMemberById() {
        // given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        // when
        Member result = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new IllegalArgumentException("잘못된 아이디 입니다."));

        // then
        assertThat(result.getName()).isEqualTo("hong");
    }

    @DisplayName("이메일을 전달 받아 회원정보를 조회한다.")
    @Test
    void findMemberByEmail() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Member result = memberRepository.findByEmail(member.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 존재하지 않습니다."));

        // then
        assertThat(result.getName()).isEqualTo("hong");
    }

    @DisplayName("중복 이메일이 존재하는 지 검증한다.")
    @Test
    void existsEmail() {
        assertThat(memberRepository.existsByEmail("iphone@gmail.com")).isTrue();
    }

    private Member createMember() {
        return Member.builder()
            .email("hong@gmail.com")
            .password("zxcv1234")
            .name("hong")
            .build();
    }
}
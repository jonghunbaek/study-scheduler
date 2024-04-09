package toyproject.studyscheduler.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import toyproject.studyscheduler.member.application.dto.SignUpInfo;
import toyproject.studyscheduler.member.domain.entity.Member;
import toyproject.studyscheduler.member.exception.MemberException;
import toyproject.studyscheduler.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("회원 정보를 전달 받아 회원 가입을 한다.")
    @Test
    void signUp() {
        // given
        SignUpInfo signUpInfo = SignUpInfo.builder()
            .email("abc@gmail.com")
            .password("12345")
            .name("abc")
            .build();

        // when
        memberService.saveNewMember(signUpInfo);
        Member member = memberRepository.findAll().get(0);

        // then
        assertThat(member.getEmail()).isEqualTo("abc@gmail.com");
    }

    @DisplayName("회원 가입 시 동일한 이메일이 DB에 존재하면 예외를 발생한다.")
    @Test
    void signUpWhenExistsSameEmail() {
        // given
        memberRepository.save(Member.builder()
            .email("abc@gmail.com")
            .build());

        SignUpInfo signUpInfo = SignUpInfo.builder()
            .email("abc@gmail.com")
            .password("12345")
            .name("abc")
            .build();

        // when & then
        assertThatThrownBy(() -> memberService.saveNewMember(signUpInfo))
            .isInstanceOf(MemberException.class)
            .hasMessage("이미 존재하는 이메일이 있습니다.");
    }
}
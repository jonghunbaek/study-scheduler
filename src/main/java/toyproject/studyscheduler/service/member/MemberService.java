package toyproject.studyscheduler.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toyproject.studyscheduler.controller.request.member.SignUp;
import toyproject.studyscheduler.controller.request.member.SignIn;
import toyproject.studyscheduler.domain.member.Member;
import toyproject.studyscheduler.domain.member.repository.MemberRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public void saveMember(SignUp requestDto) {
        if (isEmailExist(requestDto.getEmail())) {
            throw new IllegalArgumentException("해당 이메일이 이미 존재합니다.");
        }

        memberRepository.save(requestDto.toEntity());
    }

    private boolean isEmailExist(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public Member signIn(SignIn signIn) {
        Member member = memberRepository.findByEmail(signIn.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 계정이 존재하지 않습니다."));

        if (member.isMatching(signIn.getPassword())) {
            return member;
        }

        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
}

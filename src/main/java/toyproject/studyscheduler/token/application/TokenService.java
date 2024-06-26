package toyproject.studyscheduler.token.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toyproject.studyscheduler.common.response.ResponseCode;
import toyproject.studyscheduler.member.domain.Role;
import toyproject.studyscheduler.token.application.dto.TokenCreationInfo;
import toyproject.studyscheduler.auth.web.dto.Tokens;
import toyproject.studyscheduler.common.jwt.JwtManager;
import toyproject.studyscheduler.token.domain.BlackToken;
import toyproject.studyscheduler.token.domain.entity.RefreshToken;
import toyproject.studyscheduler.token.exception.TokenException;
import toyproject.studyscheduler.token.repository.RefreshTokenRepository;
import toyproject.studyscheduler.token.repository.redis.BlackTokenRepository;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class TokenService {

    // TODO :: 추후 시간되면 토큰 매니저를 추상화하기
    private final JwtManager jwtManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackTokenRepository blackTokenRepository;

    @Transactional
    public Tokens createTokens(TokenCreationInfo tokenCreationInfo) {
        long memberId = tokenCreationInfo.getMemberId();
        Role role = tokenCreationInfo.getRole();

        Instant now = Instant.now();
        String accessToken = jwtManager.createAccessToken(memberId, role, now);
        String refreshToken = jwtManager.createRefreshToken(now);

        saveRefreshToken(memberId, refreshToken);

        return Tokens.of(accessToken, refreshToken);
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        refreshTokenRepository.findById(memberId)
            .ifPresentOrElse(
                refresh -> refresh.updateNewToken(refreshToken),
                () -> refreshTokenRepository.save(new RefreshToken(memberId, refreshToken))
            );
    }

    /**
     * Access Token, Refresh Token 재발행
     * Refresh Token 파싱으로 만료시간, 위조 여부 1차검증
     * DB에 저장된 Refresh Token과 비교해 2차 검증을 한다.
     * DB에 저장된 값과 다르면 이미 재발행된 토큰이기 때문에 위조의 가능성이 생김
     * @return 새로운 access token과 기존 refresh token
     */
    @Transactional
    public Tokens reissueTokens(String accessTokens, String refreshToken) {
        Instant now = Instant.now();
        String newRefreshToken = reissueRefreshToken(refreshToken, now);
        String newAccessToken = jwtManager.reissueAccessToken(accessTokens, now);

        return Tokens.of(newAccessToken, newRefreshToken);
    }

    private String reissueRefreshToken(String refreshToken, Instant now) {
        jwtManager.validateRefreshToken(refreshToken);
        RefreshToken entity = findRefreshToken(refreshToken);

        String newRefresh = jwtManager.createRefreshToken(now);

        entity.updateNewToken(newRefresh);
        return newRefresh;
    }

    private RefreshToken findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenException(ResponseCode.E20000));
    }

    @Transactional
    public void blockTokens(String accessToken) {
        long expiration = jwtManager.calculateExpirationSec(accessToken, Instant.now());
        blackTokenRepository.save(new BlackToken(accessToken, expiration));

        String[] idAndRole = jwtManager.parseAccessToken(accessToken);
        refreshTokenRepository.deleteById(Long.parseLong(idAndRole[0]));
    }
}

package toyproject.studyscheduler.auth.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import toyproject.studyscheduler.auth.application.AuthService;
import toyproject.studyscheduler.common.util.CookieManager;
import toyproject.studyscheduler.token.application.TokenService;
import toyproject.studyscheduler.token.application.dto.TokenCreationInfo;
import toyproject.studyscheduler.auth.application.dto.SignInInfo;
import toyproject.studyscheduler.auth.application.dto.SignUpInfo;
import toyproject.studyscheduler.auth.web.dto.Tokens;

import static org.springframework.http.HttpHeaders.*;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String[] TOKEN_TYPE = {"access_token", "refresh_token"};

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/auth/sign-up")
    public void signUp(@RequestBody SignUpInfo signUpInfo) {
        authService.saveNewMember(signUpInfo);
    }

    @PostMapping("/auth/sign-in")
    public void signIn(@RequestBody SignInInfo signInInfo, HttpServletResponse response) {
        TokenCreationInfo tokenCreationInfo = authService.login(signInInfo);
        Tokens tokens = tokenService.createTokens(tokenCreationInfo);
        CookieManager.setUpTokensToCookie(tokens, response);
    }


}

package toyproject.studyscheduler.auth.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import toyproject.studyscheduler.auth.application.AuthService;
import toyproject.studyscheduler.auth.application.dto.SignInInfo;
import toyproject.studyscheduler.auth.web.dto.Tokens;
import toyproject.studyscheduler.common.jwt.JwtAuthenticationFilter;
import toyproject.studyscheduler.token.application.TokenService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)}
)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthService authService;
    @MockBean
    TokenService tokenService;

    @WithMockUser
    @DisplayName("로그인 시 access, refresh token을 쿠키에 담아 반환한다.")
    @Test
    void setUpCookiesWhenSignIn() throws Exception {
        // given
        SignInInfo signInInfo = new SignInInfo("abc@gmail.com", "password");
        String accessToken = "1234";
        String refreshToken = "123456";
        Tokens tokens = Tokens.of(accessToken, refreshToken);

        when(tokenService.createTokens(any()))
                .thenReturn(tokens);

        // when & then
        mockMvc.perform(post("/auth/sign-in")
                .content(objectMapper.writeValueAsString(signInInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(cookie().value("access_token", accessToken))
                .andExpect(cookie().value("refresh_token", refreshToken));
    }

    @WithMockUser
    @DisplayName("로그아웃 요청이 들어오면 쿠키에서 access, refresh token의 값을 삭제한다.")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .header(AUTHORIZATION, "1234")
                .cookie(new Cookie("refresh_token", "123456"))
                .cookie(new Cookie("access_token", "1234"))
                .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(cookie().value("access_token", ""))
            .andExpect(cookie().value("refresh_token", ""));
    }
}
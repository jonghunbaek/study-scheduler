package toyproject.studyscheduler.dailystudy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;
import toyproject.studyscheduler.common.jwt.JwtAuthenticationFilter;
import toyproject.studyscheduler.common.response.ResponseForm;
import toyproject.studyscheduler.dailystudy.application.DailyStudyService;
import toyproject.studyscheduler.dailystudy.application.dto.DailyStudySave;
import toyproject.studyscheduler.dailystudy.application.dto.DailyStudyUpdate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toyproject.studyscheduler.common.response.ResponseCode.*;

@WebMvcTest(
        controllers = DailyStudyController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)}
)
class DailyStudyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    DailyStudyService dailyStudyService;

    @WithMockUser
    @DisplayName("일일 학습을 등록할 때 body에 전달되는 값에 대한 입력 검증을 한다.")
    @ParameterizedTest
    @MethodSource("argumentsWhenDailyStudyCreate")
    void getStudiesByPeriod(DailyStudySave dailyStudySave, ResponseForm response) throws Exception {
        // given
        String jsonResponse = objectMapper.writeValueAsString(response);

        // when & then
        mockMvc.perform(post("/daily-studies")
                        .content(objectMapper.writeValueAsString(dailyStudySave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));
    }

    private static Stream<Arguments> argumentsWhenDailyStudyCreate() {
        return Stream.of(
                Arguments.of(
                        DailyStudySave.builder()
                                .studyId(-1L)
                                .content("테스트 내용입니다.")
                                .completeMinutesToday(1)
                                .studyDate(LocalDate.of(2024, 4, 1))
                                .build(),
                        ResponseForm.from(E90000, Map.of("studyId", "학습 id의 최소 값은 1입니다."))
                ),
                Arguments.of(
                        DailyStudySave.builder()
                                .studyId(null)
                                .content(createStringOver1000Characters())
                                .completeMinutesToday(-1)
                                .studyDate(null)
                                .build(),
                        ResponseForm.from(E90000, Map.of(
                                "studyId", "학습 id는 필수 값입니다.",
                                "content", "학습 내용은 1000자 이하이어야 합니다.",
                                "completeMinutesToday", "최소 학습 시간은 1분입니다.",
                                "studyDate", "학습일은 필수 입력 값입니다."
                                )
                        )
                )
        );
    }

    private static String createStringOver1000Characters() {
        StringBuilder sb = new StringBuilder("한글로");

        while (sb.length() < 1000) {
            sb.append(sb);
        }

        return sb.toString();
    }

    @WithMockUser
    @DisplayName("예상 종료일을 조회할 때 요청 파라미터의 값을 검증한다.")
    @ParameterizedTest
    @MethodSource("argumentsWhengetExpectedEndDate")
    void getExpectedEndDate(String studyId, ResponseForm response) throws Exception {
        // given
        String jsonResponse = objectMapper.writeValueAsString(response);

        // when & then
        mockMvc.perform(get("/daily-studies/remaining")
                        .queryParam("studyId", studyId)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));
    }

    private static Stream<Arguments> argumentsWhengetExpectedEndDate() {
        return Stream.of(
                Arguments.of(
                        "",
                        ResponseForm.of(E90002)
                ),
                Arguments.of(
                        "-1",
                        ResponseForm.from(E90000, List.of("id 값은 양의 정수이어야 합니다."))
                ),
                Arguments.of(
                        "TEST",
                        ResponseForm.of(E90001)
                )
        );
    }

    @WithMockUser
    @DisplayName("일일 학습을 수정할 때 입력 값을 검증한다.")
    @ParameterizedTest
    @MethodSource("argumentsWhenDailyStudyUpdate")
    void dailyStudyUpdate(String dailyStudyId, DailyStudyUpdate dailyStudyUpdate, ResponseForm response) throws Exception {
        // given
        String jsonResponse = objectMapper.writeValueAsString(response);

        ResultActions resultActions = mockMvc.perform(put("/daily-studies" + dailyStudyId)
                .content(objectMapper.writeValueAsString(dailyStudyUpdate))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
            )
            .andDo(print());


        // when & then
        if (StringUtils.hasText(dailyStudyId)) {
            resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));
        } else {
            resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().json(jsonResponse));
        }
    }

    private static Stream<Arguments> argumentsWhenDailyStudyUpdate() {
        return Stream.of(
            Arguments.of(
                "",
                DailyStudyUpdate.builder()
                    .content("정상 내용")
                    .studyDate(LocalDate.of(2024, 5, 1))
                    .completeMinutesToday(100)
                    .build(),
                ResponseForm.of(E90003)
            ),
            Arguments.of(
                "/-1",
                DailyStudyUpdate.builder()
                    .content("정상 내용")
                    .studyDate(LocalDate.of(2024, 5, 1))
                    .completeMinutesToday(100)
                    .build(),
                ResponseForm.from(E90000, List.of("id 값은 양의 정수이어야 합니다."))
            ),
            Arguments.of(
                "/TEST",
                DailyStudyUpdate.builder()
                    .content("정상 내용")
                    .studyDate(LocalDate.of(2024, 5, 1))
                    .completeMinutesToday(100)
                    .build(),
                ResponseForm.of(E90001)
            ),
            Arguments.of(
                "/1",
                DailyStudyUpdate.builder()
                        .content(createStringOver1000Characters())
                        .studyDate(null)
                        .completeMinutesToday(0)
                        .build(),
                ResponseForm.from(E90000, Map.of(
                    "content", "학습 내용은 1000자 이하이어야 합니다.",
                    "studyDate", "학습일은 필수 입력 값입니다.",
                    "completeMinutesToday", "최소 학습 시간은 1분입니다."
                ))
            )
        );
    }

    @WithMockUser
    @DisplayName("학습 ID와 기간을 조건으로 일일 학습을 조회할 때 입력 값을 검증한다.")
    @ParameterizedTest
    @MethodSource("argumentsWhenDailyStudyBasicInfos")
    void dailyStudyBasicInfos(String studyId, String startDate, String endDate, ResponseForm response) throws Exception {
        // given
        String jsonResponse = objectMapper.writeValueAsString(response);

        // when & then
        mockMvc.perform(get("/daily-studies/study")
                        .queryParam("studyId", studyId)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonResponse));
    }

    private static Stream<Arguments> argumentsWhenDailyStudyBasicInfos() {
        return Stream.of(
                Arguments.of(
                        "1",
                        "2024-04-01",
                        "2024-03-31",
                        ResponseForm.from(E90000, Map.of(
                                "period", "종료일은 시작일보다 나중이어야 합니다."
                        ))
                )
        );
    }
}
package tutorial.board2.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import tutorial.board2.global.response.Response;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WebMvcTest {

    @InjectMocks TestController testController;
    MockMvc mockMvc; // 컨트롤러로 요청을 보냄

    @Controller // 테스트 용도의 컨트롤러 생성
    public static class TestController {
        // Response 객체의 result 필드가 null 일 경우 필드 없음.
        // 응답 json 에 result 필드가 없음을 확인
        @GetMapping("/test/ignore-null-value")
        public Response ignoreNullValueTest() {
            return Response.success();
        }
    }

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void ignoreNullValueInJsonResponseTest() throws Exception {
        mockMvc.perform(
                        get("/test/ignore-null-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").doesNotExist());
    }
}
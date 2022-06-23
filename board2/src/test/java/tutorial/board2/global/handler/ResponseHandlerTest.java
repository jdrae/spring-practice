package tutorial.board2.global.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import tutorial.board2.global.response.Failure;
import tutorial.board2.global.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static tutorial.board2.global.advice.ExceptionType.BIND_EXCEPTION;
import static tutorial.board2.global.advice.ExceptionType.EXCEPTION;

public class ResponseHandlerTest {
    ResponseHandler responseHandler;

    @BeforeEach
    void beforeEach() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        responseHandler = new ResponseHandler(messageSource);
    }

    @Test
    void getFailureResponseNoArgsTest() {
        // given, when
        Response failureResponse = responseHandler.getFailureResponse(EXCEPTION);

        // then
        assertThat(failureResponse.getCode()).isEqualTo(-1000);
        assertThat(((Failure) failureResponse.getResult()).getMsg()).isEqualTo("오류가 발생하였습니다.");
    }

    @Test
    void getFailureResponseWithArgsTest() {
        // given, when
        Response failureResponse = responseHandler.getFailureResponse(BIND_EXCEPTION, "my args");

        // then
        assertThat(failureResponse.getCode()).isEqualTo(-1003);
        assertThat(((Failure) failureResponse.getResult()).getMsg()).isEqualTo("my args");
    }
}

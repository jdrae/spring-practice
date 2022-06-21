package tutorial.board2.domain.account.dto;


import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static tutorial.board2.factory.dto.SignUpRequestFactory.*;

class SignUpRequestValidationTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        SignUpRequest req = createSignUpRequest();

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByEmptyPasswordTest() {
        // given
        String invalidValue = null;
        SignUpRequest req = createSignUpRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankPasswordTest() {
        // given
        String invalidValue = "        ";
        SignUpRequest req = createSignUpRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByShortPasswordTest() {
        // given
        String invalidValue = "12312a!";
        SignUpRequest req = createSignUpRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNoneAlphabetPasswordTest() {
        // given
        String invalidValue = "123!@#123";
        SignUpRequest req = createSignUpRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNoneNumberPasswordTest() {
        // given
        String invalidValue = "abc!@#abc";
        SignUpRequest req = createSignUpRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNoneSpecialCasePasswordTest() {
        // given
        String invalidValue = "abc123abc";
        SignUpRequest req = createSignUpRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyUsernameTest() {
        // given
        String invalidValue = null;
        SignUpRequest req = createSignUpRequestWithUsername(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankUsernameTest() {
        // given
        String invalidValue = " ";
        SignUpRequest req = createSignUpRequestWithUsername(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByShortUsernameTest() {
        // given
        String invalidValue = "한";
        SignUpRequest req = createSignUpRequestWithUsername(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNotAlphabetOrHangeulUsernameTest() {
        // given
        String invalidValue = "송2jae";
        SignUpRequest req = createSignUpRequestWithUsername(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyNicknameTest() {
        // given
        String invalidValue = null;
        SignUpRequest req = createSignUpRequestWithNickname(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankNicknameTest() {
        // given
        String invalidValue = " ";
        SignUpRequest req = createSignUpRequestWithNickname(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByShortNicknameTest() {
        // given
        String invalidValue = "한";
        SignUpRequest req = createSignUpRequestWithNickname(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNotAlphabetOrHangeulNicknameTest() {
        // given
        String invalidValue = "송2jae";
        SignUpRequest req = createSignUpRequestWithNickname(invalidValue);

        // when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }
}
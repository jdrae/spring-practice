package tutorial.board.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    private static String PASSWORD = "ThePassword";

    @Test
    void 패스워드_암호화() throws Exception {
        //given

        //when
        String encodedPassword = passwordEncoder.encode(PASSWORD);

        //then
        assertThat(encodedPassword).startsWith("{");
        assertThat(encodedPassword).contains("{bcrypt}");
        assertThat(encodedPassword).isNotEqualTo(PASSWORD);
    }

    @Test
    void 패스워드_랜덤_암호화() throws Exception {
        //given

        //when
        String encoded1 = passwordEncoder.encode(PASSWORD);
        String encoded2 = passwordEncoder.encode(PASSWORD);

        //then
        assertThat(encoded1).isNotEqualTo(encoded2);
    }

    @Test
    void 암호화된_비밀번호_매치() throws Exception {
        //given

        //when
        String encodedPassword = passwordEncoder.encode(PASSWORD);

        //then
        assertThat(passwordEncoder.matches(PASSWORD, encodedPassword)).isTrue();
    }
}

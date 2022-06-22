package tutorial.board2.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/exception/**",
                "/swagger-ui/**", "/swagger-resources/**","/v3/api-docs/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()//formLogin 인증방법 비활성화
                .httpBasic().disable()//httpBasic 인증방법 비활성화(특정 리소스에 접근할 때 username과 password 물어봄)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/sign-in", "/api/sign-up",  "/api/refresh-token").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/sign-in/", "/api/sign-up/",  "/api/refresh-token/").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                    // "@<빈이름>.<메소드명>(<인자>)"
                    .antMatchers(HttpMethod.DELETE, "/api/members/{id}/**").access("@memberGuard.check(#id)")
                    .anyRequest().hasAnyRole("ADMIN")

                // 핸들러는 컨트롤러 전에 수행되기 때문에, ExceptionAdvice 에서는 예외를 잡지 않는다
                // /exception/{예외} 로 리다이렉트 하고, 예외를 발생시켜 ExceptionAdvice 클래스가 잡도록 함
                .and() // 인증되지 않은 사용자의 접근 거부
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and() // 인증된 사용자의 (권한부족 등 이유로) 접근 거부
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())

                .and() // 직접 정의한 jwt 필터 추가
                    .addFilterBefore(new JwtAuthenticationFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class)
                ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

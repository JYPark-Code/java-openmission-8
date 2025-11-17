package woowatech8.openmission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 책에서는 AntPathRequestMatcher를 쓰는데 Deprecated라 해당 부분 change.
//        http.authorizeHttpRequests((authorizeHttpRequests) ->
//                authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
//                .csrf((csrf) -> csrf
//                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
//                        .headers((headers) -> headers
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(
//                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
//                .formLogin((formLogin) -> formLogin
//                    .loginPage("/user/login")
//                    .defaultSuccessUrl("/"))
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true))
//


//        ;

        http
                // CSRF는 개발 단계에서는 꺼두면 편함. (POST 403 방지용)
//                .csrf(csrf -> csrf.disable())
                // 모든 요청 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll())

                        .formLogin((formLogin) -> formLogin
                                .loginPage("/user/login")
                                .defaultSuccessUrl("/"))
                        .logout(logout -> logout
                                .logoutUrl("/user/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                        )



        ;


        // formLogin, httpBasic는 disable 안 해도,
        // anyRequest().authenticated() 같은 게 없으면 로그인 창으로 리다렉트되지 않음.


        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

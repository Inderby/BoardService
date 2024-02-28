package com.example.boardservice.config;

import com.example.boardservice.dto.UserAccountDto;
import com.example.boardservice.dto.security.BoardPrincipal;
import com.example.boardservice.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() //정적 자원에 대한 보안 요소를 Spring Security 관리하에 두도록 한다.
                        .requestMatchers(HttpMethod.GET,
                                "/"
                                , "/articles"
                                ,"/articles/search-hashtag", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    //TODO : 예전에 사용되던 방식 지금은 httpSecurity
//    @Bean
//    WebSecurityCustomizer webSecurityCustomizer(){
//        //static resource
//        return (web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()));//static이 담기는 범위를 전부 잡은
//    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository){
        return (username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                .orElseThrow( () -> new UsernameNotFoundException("유저를 찾을 수 없습니다"))
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

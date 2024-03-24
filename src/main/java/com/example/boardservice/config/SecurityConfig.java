package com.example.boardservice.config;

import com.example.boardservice.dto.security.BoardPrincipal;
import com.example.boardservice.dto.security.KakaoOAuth2Response;
import com.example.boardservice.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        http
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers( PathRequest.toStaticResources().atCommonLocations()).permitAll() //정적 자원에 대한 보안 요소를 Spring Security 관리하에 두도록 한다.
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers( HttpMethod.GET,
                                "/"
                                , "/articles"
                                ,"/articles/search-hashtag", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ){
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoOAuth2Response = KakaoOAuth2Response.from(oAuth2User.getAttributes());
            String registrationId = userRequest.getClientRegistration().getRegistrationId(); //예상 값 : "kakao"
            String providerId = String.valueOf(kakaoOAuth2Response.id());
            String username = registrationId + "_" + providerId;
            String dummyPassword = passwordEncoder().encode("{bcrypt}" + UUID.randomUUID());
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)
                    .orElseGet(()->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoOAuth2Response.email(),
                                            kakaoOAuth2Response.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    //TODO : 예전에 사용되던 방식 지금은 httpSecurity
//    @Bean
//    WebSecurityCustomizer webSecurityCustomizer(){
//        //static resource
//        return (web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()));//static이 담기는 범위를 전부 잡은
//    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService){
        return (username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow( () -> new UsernameNotFoundException("유저를 찾을 수 없습니다"))
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

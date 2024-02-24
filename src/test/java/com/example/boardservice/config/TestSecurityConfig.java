package com.example.boardservice.config;

import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
@Import(SecurityConfig.class)
public class TestSecurityConfig { //컨트롤러 테스트와 관련하여 UserAccount repository의 인증 정보를 자동으로 추가해줄 수 있도록 도와주는 설정
    @MockBean private UserAccountRepository userAccountRepository;

    @BeforeTestMethod //테스트 코드에 한정해서 인증 정보를 넣어줄 수 있게 해주는 어노테이샨
    public void securitySetUp(){
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "inderby",
                "pw",
                "test@email.com",
                "test",
                "test memo"
        )));
    }
}

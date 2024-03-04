package com.example.boardservice.service;

import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.dto.UserAccountDto;
import com.example.boardservice.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Optional<UserAccountDto> searchUser(String username){
        return userAccountRepository.findById(username)
                .map(UserAccountDto::from);
        /*
        orElse를 통해 Exception을 service layer에서 처리하지 않는 것으로 결정.
        그 이유는 어떤 예외를 처리할지 해당 서비스에서 결정하지 않겠다는 의미이다.
        통상적으로 EntityNotFoundException을 통해 처리하지만 해당 로직은
        인증과 관련된 부분이기 때문에 UsernameNotFoundException 에러를 던져야 하는 경우가 생긴다.
        때문에 코드를 사용하는 호출자 입장에 따라서 처리를 다르게 해줘야 한다.
        */
    }
    public UserAccountDto saveUser(String username, String password, String email, String nickname, String memo){
        return UserAccountDto.from(userAccountRepository.save(UserAccount.of(username, password, email, nickname, memo, username)));
    }
}

package com.example.boardservice.service;

import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.dto.UserAccountDto;
import com.example.boardservice.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {
    @InjectMocks private UserAccountService sut;

    @Mock private UserAccountRepository userAccountRepository;

    private TestFixture fixture = new TestFixture();

    @DisplayName("존재하는 회원 ID를 검색하면, 회원 데이터를 Optional로 반환한다.")
    @Test
    void givenExistentUserId_whenSearching_thenReturnsOptionalUserData(){
        //Given
        String username = "inderby";
        given(userAccountRepository.findById(username)).willReturn(Optional.of(fixture.createUserAccount(username)));
        //When
        Optional<UserAccountDto> result = sut.searchUser(username);
        //Then
        assertThat(result).isPresent();
        then(userAccountRepository).should().findById(username);
    }

    @DisplayName("존재하지 않는 회원 ID로 검색하면, 비어있는 Optional을 반환한다.")
    @Test
    void given_when_then(){
        //Given
        String username = "wrong-user";
        given(userAccountRepository.findById(username)).willReturn(Optional.empty());
        //When
        Optional<UserAccountDto> result = sut.searchUser(username);
        //Then
        assertThat(result).isEmpty();
        then(userAccountRepository).should().findById(username);
    }

    @DisplayName("회원 정보를 입력하면, 새로운 회원 정보를 저장하여 가입시키고 해당 회원 데이터를 리턴한다.")
    @Test
    void givenUserParams_whenSaving_thenSavesUserAccount(){
        //Given
        UserAccount userAccount = fixture.createUserAccount("inderby");
        UserAccount savedUserAccount = fixture.createSigningUpUserAccount("inderby");
        given(userAccountRepository.save(userAccount)).willReturn(savedUserAccount);
        //When
        UserAccountDto result = sut.saveUser(
                userAccount.getUserId(),
                userAccount.getUserPassword(),
                userAccount.getEmail(),
                userAccount.getNickname(),
                userAccount.getMemo()
        );
        //Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("userId", userAccount.getUserId())
                .hasFieldOrPropertyWithValue("userPassword", userAccount.getUserPassword())
                .hasFieldOrPropertyWithValue("email", userAccount.getEmail())
                .hasFieldOrPropertyWithValue("nickname", userAccount.getNickname())
                .hasFieldOrPropertyWithValue("memo", userAccount.getMemo())
                .hasFieldOrPropertyWithValue("createdBy", userAccount.getUserId())
                .hasFieldOrPropertyWithValue("modifiedBy", userAccount.getUserId());
        then(userAccountRepository).should().save(userAccount);
    }
}
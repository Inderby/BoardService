package com.example.boardservice.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest //내부적으로 Mock MVC를 사용할 수 있게 해주는 어노테이션
@Disabled("Spring Data REST 통합 테스트는 불필요하므로 제외시킴")
@DisplayName("Data REST - api 테스트")
@Transactional // test에서 동작하는 transactional의 동작은 기본이 rollback이기 때문에 db에 영향을 주지 않는 테스트로 설정해줄 수 있다.
@AutoConfigureMockMvc //MockMvc를 사용하기 위한 어노테이션 설정
@SpringBootTest
public class DataRestTest {
    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 리스트 테스트 조회")
    @Test
    void givenNothing_whenRequestingArticles_thenReturnsArticlesJsonResponse() throws Exception {
        //Given

        //When&Then
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        //@WebMvcTest 환경이라면 해당 테스트는 실패 된다. 그 이유는 WebMvcTest는 slice test이기 때문에 컨트롤러 외의 빈들은 load하지 않는다. 즉 data rest의 auto configuration들을 로드하지 않은 것이다.
    }

    @DisplayName("[api] 게시글 리스트 단건 조회")
    @Test
    void givenNothing_whenRequestingArticle_thenReturnsArticlesJsonResponse() throws Exception {
        //Given

        //When&Then
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        //@WebMvcTest 환경이라면 해당 테스트는 실패 된다. 그 이유는 WebMvcTest는 slice test이기 때문에 컨트롤러 외의 빈들은 load하지 않는다. 즉 data rest의 auto configuration들을 로드하지 않은 것이다.
    }

    @DisplayName("[api] 게시글 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleCommentsFromArticle_thenReturnsArticleCommentsJsonResponse() throws Exception {
        //Given

        //When&Then
        mvc.perform(get("/api/articles/1/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        //@WebMvcTest 환경이라면 해당 테스트는 실패 된다. 그 이유는 WebMvcTest는 slice test이기 때문에 컨트롤러 외의 빈들은 load하지 않는다. 즉 data rest의 auto configuration들을 로드하지 않은 것이다.
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleComments_thenReturnsArticlesJsonResponse() throws Exception {
        //Given

        //When&Then
        mvc.perform(get("/api/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        //@WebMvcTest 환경이라면 해당 테스트는 실패 된다. 그 이유는 WebMvcTest는 slice test이기 때문에 컨트롤러 외의 빈들은 load하지 않는다. 즉 data rest의 auto configuration들을 로드하지 않은 것이다.
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticleComment_thenReturnsArticleJsonResponse() throws Exception {
        //Given

        //When&Then
        mvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        //@WebMvcTest 환경이라면 해당 테스트는 실패 된다. 그 이유는 WebMvcTest는 slice test이기 때문에 컨트롤러 외의 빈들은 load하지 않는다. 즉 data rest의 auto configuration들을 로드하지 않은 것이다.
    }

    @DisplayName("[api] 회원 관련 API는 일체 제공하지 않는다.")
    @Test
    void givenNothing_whenRequestingUserAccount_thenThrowsException() throws Exception{
        //Given

        //When&Then
        mvc.perform(get("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(post("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(put("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(patch("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(head("/api/userAccounts")).andExpect(status().isNotFound());
    }

}

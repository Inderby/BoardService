package com.example.boardservice.service;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.ArticleComment;
import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.dto.ArticleCommentDto;
import com.example.boardservice.dto.ArticleDto;
import com.example.boardservice.dto.ArticleWithCommentsDto;
import com.example.boardservice.dto.UserAccountDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class TestFixture {
    public ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
                LocalDateTime.now(),
                "inderby",
                LocalDateTime.now(),
                "inderby"
        );
    }

    public UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "inderby",
                "password",
                "inderby@mail.com",
                "inderby",
                "This is memo",
                LocalDateTime.now(),
                "inderby",
                LocalDateTime.now(),
                "inderby"
        );
    }

    public ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                Article.of(createUserAccount(), "title", "content", "hashtag"),
                createUserAccount(),
                content
        );
    }

    public UserAccount createUserAccount() {
        return UserAccount.of(
                "inderby",
                "password",
                "inderby@email.com",
                "inderby",
                null
        );
    }

    public Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }
    public ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    public ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "inderby",
                LocalDateTime.now(),
                "inderby");
    }

    public ArticleWithCommentsDto createArticleWithCommentDto(){
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "inderby",
                LocalDateTime.now(),
                "inderby"
        );
    }
}

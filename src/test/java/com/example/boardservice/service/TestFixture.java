package com.example.boardservice.service;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.ArticleComment;
import com.example.boardservice.domain.Hashtag;
import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

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
                Article.of(createUserAccount(), "title", "content"),
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
                "content"
        );
    }
    public ArticleDto createArticleDto() {
        return createArticleDto("title", "content");
    }

    public ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                Set.of(HashtagDto.of("java")),
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
                Set.of(HashtagDto.of("java")),
                LocalDateTime.now(),
                "inderby",
                LocalDateTime.now(),
                "inderby"
        );
    }

    public Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    public Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);

        return hashtag;
    }

    public HashtagDto createHashtagDto() {
        return HashtagDto.of("java");
    }

    public Article createArticle(Long id) {
        Article article = Article.of(
                createUserAccount(),
                "title",
                "content"
        );
        article.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));
        ReflectionTestUtils.setField(article, "id", id);

        return article;
    }

    public UserAccount createUserAccount(String userId) {
        return UserAccount.of(
                userId,
                "password",
                "inderby@email.com",
                "Inderby",
                null
        );
    }
}

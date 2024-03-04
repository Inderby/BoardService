package com.example.boardservice.service;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.ArticleComment;
import com.example.boardservice.domain.Hashtag;
import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.dto.*;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Set;

@TestComponent
public class TestFixture {

    public ArticleCommentDto createArticleCommentDto(String content) {
        return createArticleCommentDto(null, content);
    }
    public ArticleCommentDto createArticleCommentDto(Long parentCommentId, String content){
        return createArticleCommentDto(1L, parentCommentId, content);
    }
    public ArticleCommentDto createArticleCommentDto(Long id, Long parentCommentId, String content) {
        return ArticleCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
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

    public ArticleComment createArticleComment(Long id, String content) {
        ArticleComment articleComment = ArticleComment.of(
                createArticle(),
                createUserAccount(),
                content
        );
        ReflectionTestUtils.setField(articleComment, "id", id);
        return articleComment;
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
        return createArticle(1L);
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
                "memo"
        );
    }

    public UserAccount createSigningUpUserAccount(String username){
        return createUserAccount(username, username);
    }

    public UserAccount createUserAccount(String username, String createdBy){
        return UserAccount.of(
                username,
                "password",
                "inderby@email.com",
                "Inderby",
                "memo",
                createdBy
        );
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto(Set<ArticleCommentDto> articleCommentDtos) {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                articleCommentDtos,
                "title",
                "content",
                Set.of(HashtagDto.of("java")),
                LocalDateTime.now(),
                "inderby",
                LocalDateTime.now(),
                "inderby"
        );
    }



    public ArticleCommentDto createArticleCommentDto(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return ArticleCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
                "test comment " + id,
                createdAt,
                "inderby",
                createdAt,
                "inderby"
        );
    }

    public ArticleCommentResponse createArticleCommentResponse(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return ArticleCommentResponse.of(
                id,
                "test comment " + id,
                createdAt,
                "inderby@mail.com",
                "inderby",
                "inderby",
                parentCommentId
        );
    }
}

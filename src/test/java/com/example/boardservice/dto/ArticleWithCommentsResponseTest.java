package com.example.boardservice.dto;

import com.example.boardservice.service.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("DTO - 댓글을 포함한 게시글 응답 테스트")
class ArticleWithCommentsResponseTest {
    private final TestFixture fixture = new TestFixture();



    @DisplayName("자식 댓글이 없는 게시글 + 댓글 dto를 api 응답으로 변환할 때, 댓글을 시간 내림차순 + ID 오름차순으로 정리한다.")
    @Test
    void givenArticleWithCommentsDtoWithoutChildComments_whenMapping_thenOrganizesCommentsWithCertainOrder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                fixture.createArticleCommentDto(1L, null, now),
                fixture.createArticleCommentDto(2L, null, now.plusDays(1L)),
                fixture.createArticleCommentDto(3L, null, now.plusDays(3L)),
                fixture.createArticleCommentDto(4L, null, now),
                fixture.createArticleCommentDto(5L, null, now.plusDays(5L)),
                fixture.createArticleCommentDto(6L, null, now.plusDays(4L)),
                fixture.createArticleCommentDto(7L, null, now.plusDays(2L)),
                fixture.createArticleCommentDto(8L, null, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        assertThat(actual.articleCommentsResponse())
                .containsExactly(
                        fixture.createArticleCommentResponse(8L, null, now.plusDays(7L)),
                        fixture.createArticleCommentResponse(5L, null, now.plusDays(5L)),
                        fixture.createArticleCommentResponse(6L, null, now.plusDays(4L)),
                        fixture.createArticleCommentResponse(3L, null, now.plusDays(3L)),
                        fixture.createArticleCommentResponse(7L, null, now.plusDays(2L)),
                        fixture.createArticleCommentResponse(2L, null, now.plusDays(1L)),
                        fixture.createArticleCommentResponse(1L, null, now),
                        fixture.createArticleCommentResponse(4L, null, now)
                );
    }

    @DisplayName("게시글 + 댓글 dto를 api 응답으로 변환할 때, 댓글 부모 자식 관계를 각각의 규칙으로 정렬하여 정리한다.")
    @Test
    void givenArticleWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithCertainOrders() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                fixture.createArticleCommentDto(1L, null, now),
                fixture.createArticleCommentDto(2L, 1L, now.plusDays(1L)),
                fixture.createArticleCommentDto(3L, 1L, now.plusDays(3L)),
                fixture.createArticleCommentDto(4L, 1L, now),
                fixture.createArticleCommentDto(5L, null, now.plusDays(5L)),
                fixture.createArticleCommentDto(6L, null, now.plusDays(4L)),
                fixture.createArticleCommentDto(7L, 6L, now.plusDays(2L)),
                fixture.createArticleCommentDto(8L, 6L, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        assertThat(actual.articleCommentsResponse())
                .containsExactly(
                        fixture.createArticleCommentResponse(5L, null, now.plusDays(5)),
                        fixture.createArticleCommentResponse(6L, null, now.plusDays(4)),
                        fixture.createArticleCommentResponse(1L, null, now)
                )
                .flatExtracting(ArticleCommentResponse::childComments)
                .containsExactly(
                        fixture.createArticleCommentResponse(7L, 6L, now.plusDays(2L)),
                        fixture.createArticleCommentResponse(8L, 6L, now.plusDays(7L)),
                        fixture.createArticleCommentResponse(4L, 1L, now),
                        fixture.createArticleCommentResponse(2L, 1L, now.plusDays(1L)),
                        fixture.createArticleCommentResponse(3L, 1L, now.plusDays(3L))
                );
    }

    @DisplayName("게시글 + 댓글 dto를 api 응답으로 변환할 때, 부모 자식 관계 깊이(depth)는 제한이 없다.")
    @Test
    void givenArticleWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithoutDepthLimit() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                fixture.createArticleCommentDto(1L, null, now),
                fixture.createArticleCommentDto(2L, 1L, now.plusDays(1L)),
                fixture.createArticleCommentDto(3L, 2L, now.plusDays(2L)),
                fixture.createArticleCommentDto(4L, 3L, now.plusDays(3L)),
                fixture.createArticleCommentDto(5L, 4L, now.plusDays(4L)),
                fixture.createArticleCommentDto(6L, 5L, now.plusDays(5L)),
                fixture.createArticleCommentDto(7L, 6L, now.plusDays(6L)),
                fixture.createArticleCommentDto(8L, 7L, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        Iterator<ArticleCommentResponse> iterator = actual.articleCommentsResponse().iterator();
        long i = 1L;
        while (iterator.hasNext()) {
            ArticleCommentResponse articleCommentResponse = iterator.next();
            assertThat(articleCommentResponse)
                    .hasFieldOrPropertyWithValue("id", i)
                    .hasFieldOrPropertyWithValue("parentCommentId", i == 1L ? null : i - 1L)
                    .hasFieldOrPropertyWithValue("createdAt", now.plusDays(i - 1L));

            iterator = articleCommentResponse.childComments().iterator();
            i++;
        }
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

    private UserAccountDto createUserAccountDto() {
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

}
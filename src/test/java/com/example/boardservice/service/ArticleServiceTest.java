package com.example.boardservice.service;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.constant.SearchType;
import com.example.boardservice.dto.ArticleDto;
import com.example.boardservice.dto.ArticleWithCommentsDto;
import com.example.boardservice.repository.ArticleRepository;
import com.example.boardservice.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks
    private ArticleService sut;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    private TestFixture testFixture = new TestFixture();

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지로 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlesPage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        //When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable); //제목, 본문, ID, 닉네임, 해시태그
        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지로 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticles() {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyWord = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContainingIgnoreCase(searchKeyWord, pageable)).willReturn(Page.empty());
        //When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, searchKeyWord, pageable); //제목, 본문, ID, 닉네임, 해시태그
        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContainingIgnoreCase(searchKeyWord, pageable);
    }

    @DisplayName("검색어 없이 게시글을 해시태그 검색하면, 빈 페이지로 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        //Given
        Pageable pageable = Pageable.ofSize(20);
        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable); //제목, 본문, ID, 닉네임, 해시태그
        //Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글을 해시태그 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        //Given
        String hashtag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtagIgnoreCase(hashtag, pageable)).willReturn(Page.empty(pageable));
        //When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageable); //제목, 본문, ID, 닉네임, 해시태그
        //Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtagIgnoreCase(hashtag, pageable);
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags(){
        //Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);
        //When
        List<String> actualHashtags = sut.getHashtags();
        //Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }

    @DisplayName("게시글 ID로 조회하면, 댓글 달린 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        // Given
        Long articleId = 1L;
        Article article = testFixture.createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 달린 게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticleWithComments_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        //Given
        Long articleId = 1L;
        Article article = testFixture.createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        //When
        ArticleDto dto = sut.getArticle(articleId);
        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        //Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());
        //When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));
        //Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        //Given
        ArticleDto dto = testFixture.createArticleDto();
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(testFixture.createUserAccount());
        given(articleRepository.save(any(Article.class))).willReturn(testFixture.createArticle());
        //When
        sut.saveArticle(dto);
        //Then
        then(articleRepository).should().save(any(Article.class)); //save가 호출되었는가를 검사하는 코드

    }

    @DisplayName("게시글의 수정정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        Article article = testFixture.createArticle();
        ArticleDto dto = testFixture.createArticleDto("new title", "new content", "#new hashtag");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        //getReferenceById는 findById와 비슷하게 동작하지만, findById는 select 쿼리를 통해 영속성 컨텍스트에서 가져온 뒤에 업데이트 동작을 하지만 getReferenceById는 해당 entity가 이미 DB에 있다는 전제하에 바로 update 쿼리를 날려버리는 점에서 내부 동작이 다르다.
        //getOne이 기존에 존재했지만 boot 2.7 이후로 deprecated 됨

        //When
        sut.updateArticle(dto.id(),dto);
        //Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());

    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        //Given
        ArticleDto dto = testFixture.createArticleDto("new title", "new content", "#new hashtag");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);
        //When
        sut.updateArticle(dto.id(),dto);
        //Then
        then(articleRepository).should().getReferenceById(dto.id());

    }

    @DisplayName("게시글의 ID입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        //Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId); //deleteArticle 메서드 안에 내부적으로 deleteById 메서드가 호출될 것이라는 것을 명시적으로 보여주는 코드(기능상 의미는 없다)
        //When
        sut.deleteArticle(articleId);
        //Then
        then(articleRepository).should().deleteById(articleId);

    }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다")
    @Test
    void givenNothing_whenCountingArticles_thenReturnsArticleCount() {
        // Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }
}
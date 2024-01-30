package com.example.boardservice.repository;

import com.example.boardservice.config.JpaConfig;
import com.example.boardservice.domain.Article;
import com.example.boardservice.service.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb") //자동 test db로도 잘 동작하는 테스트 이기 때문에 없어도 되는 설정일 수 있지만 공부 목적으로 남겨둠
@DisplayName("JPA 연결 테스트")
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 자동 생성된 테스트 db를 사용하지 않고 설정파일에 있는 설정을 그대로 쓴다는 의미 해당 설정읠 yaml 파일에서 설정하여 전역화 시킬 수 있다.
@Import(JpaConfig.class)
@DataJpaTest //해당 어노테이션을 통해 SpringExtension이 등록되기 때문에 RunWith를 사용하지 않더라도 생성자 주입 패턴이 들어간다.
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final TestFixture fixture = new TestFixture();

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestDate_whenSelecting_thenWorksFine(){
        //Given

        //When
        List<Article> articles = articleRepository.findAll();
        //Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestDate_whenInserting_thenWorksFine(){
        //Given
        long  previousCount = articleRepository.count();
        Article article = Article.of(fixture.createUserAccount(),"new article", "new content", "#new");
        //When
        Article savedArticle = articleRepository.save(article);

        //Then
        assertThat(articleRepository.count())
                .isNotNull()
                .isEqualTo(123 + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestDate_whenUpdating_thenWorksFine(){
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);
        //When
        Article savedArticle = articleRepository.saveAndFlush(article);
        //일반적인 slice test에서 실행되는 메소드들은 메소드 단위로 트랜잭션이 걸려있기 때문에 실제로 update 쿼리가 발생하지 않는다. 때문에 flush 키워드가 들어간 메서드를 사용한다.

        //Then
        assertThat(savedArticle)
                .hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }
    @DisplayName("delete 테스트")
    @Test
    void givenTestDate_whenDeleting_thenWorksFine(){
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        long deletedCommentsSize = article.getArticleComments().size();
        //When
        articleRepository.delete(article);
        //Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }
}
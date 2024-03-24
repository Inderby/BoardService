package com.example.boardservice.repository;

import com.example.boardservice.domain.ArticleComment;
import com.example.boardservice.domain.QArticleComment;
import com.example.boardservice.domain.projection.ArticleCommentProjection;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(excerptProjection = ArticleCommentProjection.class)
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>, //Article 안에 있는 모든 필드에 대한 기본 검색 기능을 추가해줌
        QuerydslBinderCustomizer<QArticleComment> //서비스의 입맛에 맞는 검색 기능을 추가하기 위해 사용함
{
    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root){ //spring data jpa를 이용해서 인터페이스 만을 가지고 기능을 사용하고 있기 때문에 default 키워드를 사용해 이곳에 정의하는 것이 적절해 보임
        // 이곳에서 QuerydslPredicateExecutor에 의해 열려 있는 모든 필드들에 대한 검색 기능 중에 제외 하고 싶은 것을 설정
        bindings.excludeUnlistedProperties(true); //listing하지 않은 property는 검색에서 제외하도록 설정
        bindings.including(root.content, root.createdAt, root.createdBy); //검색 기능을 지원하고 싶은 항목을 지정
        bindings.bind(root.content).first((StringExpression::containsIgnoreCase));
        bindings.bind(root.createdAt).first((DateTimeExpression::eq)); //TODO : 해당 방법은 시, 분, 초까지 동일해야 검색 대상이 되므로 이부분에 대한 수정이 필요하다
        bindings.bind(root.createdBy).first((StringExpression::containsIgnoreCase));
    }

    void deleteByIdAndUserAccount_UserId(@Param("articleCommentId") Long articleCommentId,@Param("userId") String userId);
    List<ArticleComment> findByArticle_Id(@Param("articleId") Long articleId); //articleComment의 요소가 아닌 articleComment안에 있는 article의 id를 통해 댓글을 검색하고 있음. 때문에 _(언더 스코어를 써줘서 객체 안으로 들어가도록 사)
}

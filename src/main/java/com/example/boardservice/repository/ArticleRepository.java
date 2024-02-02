package com.example.boardservice.repository;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.QArticle;
import com.example.boardservice.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@Repository JpaRepository의 구현체인 SimpleRepository에 이미 해당 어노테이션이 들어가 있기 때문에 붙이지 않아도 된다. 또한 어노테이션은 관습적으로 interface에 붙이지 않는다.
@RepositoryRestResource //Spring data rest 기능을 사용하는 annotation
public interface ArticleRepository extends JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, //Article 안에 있는 모든 필드에 대한 기본 검색 기능을 추가해줌
        QuerydslBinderCustomizer<QArticle>, //서비스의 입맛에 맞는 검색 기능을 추가하기 위해 사용함
        ArticleRepositoryCustom
{
    @Override
    default void customize(QuerydslBindings bindings, QArticle root){ //spring data jpa를 이용해서 인터페이스 만을 가지고 기능을 사용하고 있기 때문에 default 키워드를 사용해 이곳에 정의하는 것이 적절해 보임
        // 이곳에서 QuerydslPredicateExecutor에 의해 열려 있는 모든 필드들에 대한 검색 기능 중에 제외 하고 싶은 것을 설정
        bindings.excludeUnlistedProperties(true); //listing하지 않은 property는 검색에서 제외하도록 설정
        bindings.including(root.title, root.hashtag, root.content, root.createdAt, root.createdBy); //검색 기능을 지원하고 싶은 항목을 지정
        bindings.bind(root.title).first((StringExpression::containsIgnoreCase)); //대소문자를 구분하지 않고 부분 문자열에 대한 검색 기능을 지원하게 설정
        bindings.bind(root.content).first((StringExpression::containsIgnoreCase));
        bindings.bind(root.hashtag).first((StringExpression::containsIgnoreCase));
        bindings.bind(root.createdAt).first((DateTimeExpression::eq)); //TODO : 해당 방법은 시, 분, 초까지 동일해야 검색 대상이 되므로 이부분에 대한 수정이 필요하다
        bindings.bind(root.createdBy).first((StringExpression::containsIgnoreCase));
    }

    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Article> findByContentContainingIgnoreCase(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContainingIgnoreCase(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContainingIgnoreCase(String nickname, Pageable pageable); //TODO : 인덱스 지정의 의미가 사라질 수 있기 때문에 tuning 필요
    Page<Article> findByHashtagIgnoreCase(String hashtag, Pageable pageable);
}

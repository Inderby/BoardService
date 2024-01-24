package com.example.boardservice.repository;

import com.example.boardservice.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

//@Repository JpaRepository의 구현체인 SimpleRepository에 이미 해당 어노테이션이 들어가 있기 때문에 붙이지 않아도 된다. 또한 어노테이션은 관습적으로 interface에 붙이지 않는다.
@RepositoryRestResource //Spring data rest 기능을 사용하는 annotation
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
package com.example.boardservice.repository;

import com.example.boardservice.domain.Hashtag;
import com.example.boardservice.repository.querydsl.HashtagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, QuerydslPredicateExecutor<Hashtag>, HashtagRepositoryCustom {
    Optional<Hashtag> findByHashtagName(@Param("hashtagName") String hashtagName);
    List<Hashtag> findByHashtagNameIn(@Param("hashtagNames") Set<String> hashtagNames);
}

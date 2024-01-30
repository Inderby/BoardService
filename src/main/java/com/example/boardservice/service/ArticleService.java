package com.example.boardservice.service;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.type.SearchType;
import com.example.boardservice.dto.ArticleDto;
import com.example.boardservice.dto.ArticleUpdateDto;
import com.example.boardservice.dto.ArticleWithCommentsDto;
import com.example.boardservice.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType title, String search_keyword, Pageable pageable) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto searchArticle(long l) {
//        return new ArticleDto(LocalDateTime.now(), "Inderby", "new title", "new content", "#java");
        return null;
    }

    public void saveArticle(ArticleDto dto) {

    }

    public void updateArticle(ArticleDto dto) {
    }

    public void deleteArticle(long articleId) {
    }

    public Article getArticle(Long articleId) throws EntityNotFoundException {
        return null;
    }
}

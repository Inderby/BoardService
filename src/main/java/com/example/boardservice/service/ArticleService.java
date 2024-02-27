package com.example.boardservice.service;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.UserAccount;
import com.example.boardservice.domain.constant.SearchType;
import com.example.boardservice.dto.ArticleDto;
import com.example.boardservice.dto.ArticleWithCommentsDto;
import com.example.boardservice.repository.ArticleRepository;
import com.example.boardservice.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if(searchKeyword == null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType){
            case TITLE -> articleRepository.findByTitleContainingIgnoreCase(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContainingIgnoreCase(searchKeyword, pageable).map(ArticleDto::from);
            case ID-> articleRepository.findByUserAccount_UserIdContainingIgnoreCase(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContainingIgnoreCase(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagNames(
                    Arrays.stream(searchKeyword.split(" ")).toList(),pageable
            ).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) throws EntityNotFoundException {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId)); //로그를 확인할 수 있는 대상이 관리자인 안전한 환경에서만 추천
    }




    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        articleRepository.save(dto.toEntity(userAccount));
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.findById(articleId).get();
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            if(article.getUserAccount().equals(userAccount)) {
                if (dto.title() != null) {
                    article.setTitle(dto.title());
                } //java 13이후로 생긴 record에는 getter setter가 이미 들어가있다.
                if (dto.content() != null) {
                    article.setContent(dto.content());
                }
            }
            //class level transaction 에 의해서 메소드 단위로 트랜잭션이 묶여 있다. 때문에 트잭션이 끝날 때 영속성 컨텍스트가 변화에 대해 감지하고 그에 대한 query 를 날려주기 때문에 save를 쓰지 않아도 된다.
        }catch(EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다. - dto: {}", e.getLocalizedMessage());
        }
    }

    public void deleteArticle(long articleId, String userId) {
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if(hashtag == null || hashtag.isBlank()){
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtagsIgnoreCase(null, pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }
}

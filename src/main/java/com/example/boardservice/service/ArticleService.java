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

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String search_keyword, Pageable pageable) {
        if(search_keyword == null || search_keyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType){
            case TITLE -> articleRepository.findByTitleContainingIgnoreCase(search_keyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContainingIgnoreCase(search_keyword, pageable).map(ArticleDto::from);
            case ID-> articleRepository.findByUserAccount_UserIdContainingIgnoreCase(search_keyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContainingIgnoreCase(search_keyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagIgnoreCase("#" + search_keyword, pageable).map(ArticleDto::from);
            //TODO : 현재는 수동으로 사용자의 입력에 #을 직접 추가해주는 로직을 넣어줬지만 사용자가 직접 #을 추가했을 경우 2번 #이 들어갈 수 있으므로 이에 대한 처리 로직 필요
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
            Article article = articleRepository.getReferenceById(articleId);
            if (dto.title() != null) {
                article.setTitle(dto.title());
            } //java 13이후로 생긴 record에는 getter setter가 이미 들어가있다.
            if (dto.content() != null) {
                article.setContent(dto.content());
            }
            article.setHashtag(dto.hashtag()); // hashtag는 nullable이기 때문에 방어 코드를 작성하지 않는다.
            //class level transaction 에 의해서 메소드 단위로 트랜잭션이 묶여 있다. 때문에 트잭션이 끝날 때 영속성 컨텍스트가 변화에 대해 감지하고 그에 대한 query 를 날려주기 때문에 save를 쓰지 않아도 된다.
        }catch(EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다. - dto: " + dto);
        }
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if(hashtag == null || hashtag.isBlank()){
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtagIgnoreCase(hashtag, pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }
}

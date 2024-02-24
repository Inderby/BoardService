package com.example.boardservice.controller;

import com.example.boardservice.dto.request.ArticleCommentRequest;
import com.example.boardservice.dto.UserAccountDto;
import com.example.boardservice.dto.security.BoardPrincipal;
import com.example.boardservice.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comments")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(@AuthenticationPrincipal BoardPrincipal boardPrincipal,
                                        ArticleCommentRequest articleCommentRequest) {
        //TODO : 인증 정보를 넣어줘야 한다.
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(BoardPrincipal.toDto(boardPrincipal)));
        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete") //HTTP DELETE 메소드를 사용하지 않는 이유는 form 태그에서 delete나 put을 사용하지 않기 때문이다.
    public String deleteArticleComment(@PathVariable(name = "commentId") Long commentId, @RequestParam(value = "articleId") Long articleId,
                                       @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleCommentService.deleteArticleComment(commentId, boardPrincipal.getUsername());
        return "redirect:/articles/" + articleId;
    }
}

package com.example.boardservice.controller;

import com.example.boardservice.dto.ArticleCommentRequest;
import com.example.boardservice.dto.UserAccountDto;
import com.example.boardservice.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comments")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest){
        //TODO : 인증 정보를 넣어줘야 한다.
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of(
                "inderby", "1234", "example@mail.com", "inderby", "memo"
        )));
        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete") //HTTP DELETE 메소드를 사용하지 않는 이유는 form 태그에서 delete나 put을 사용하지 않기 때문이다.
    public String deleteArticleComment(@PathVariable(name ="commentId") Long commentId, @RequestParam(value="articleId")Long articleId){
        articleCommentService.deleteArticleComment(commentId);
        return "redirect:/articles/" + articleId;
    }
}

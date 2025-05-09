package k6.gatherlove.donation.comment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import k6.gatherlove.donation.comment.dto.CommentRequest;
import k6.gatherlove.donation.comment.model.Comment;
import k6.gatherlove.donation.comment.service.CommentService;

@RestController
@RequestMapping("/donations/{campaignId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment addComment(
            @PathVariable String campaignId,
            @RequestBody CommentRequest req
    ) {
        return commentService.addComment(
                campaignId,
                req.getUserId(),
                req.getText()
        );
    }

    @GetMapping
    public List<Comment> listComments(@PathVariable String campaignId) {
        return commentService.listComments(campaignId);
    }
}

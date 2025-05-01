package k6.gatherlove.donation.comment.service;

import k6.gatherlove.donation.comment.model.Comment;
import k6.gatherlove.donation.comment.repository.InMemoryCommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class CommentServiceTest {
    @Test
    void testAddAndListComments() {
        CommentService svc = new CommentServiceImpl(new InMemoryCommentRepository());
        svc.addComment("campX", "userZ", "Love this!");
        List<Comment> comments = svc.listComments("campX");

        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals("Love this!", comments.get(0).getText());
        Assertions.assertEquals("userZ",   comments.get(0).getUserId());
    }
}

package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Comment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentServiceTest {

    @Test
    void testAddAndListComments() {
        CommentService svc = new CommentServiceImpl(
                new InMemoryCommentRepository(),
                new RestTemplateBuilder()
        );

        svc.addComment("campX", "userZ", "Love this!");
        List<Comment> comments = svc.listComments("campX");

        assertEquals(1, comments.size());
        assertEquals("Love this!", comments.get(0).getText());
        assertEquals("userZ", comments.get(0).getUserId());
    }
}

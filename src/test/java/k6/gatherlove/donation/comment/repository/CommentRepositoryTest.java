package k6.gatherlove.donation.comment.repository;

import k6.gatherlove.donation.comment.model.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class CommentRepositoryTest {
    @Test
    void testSaveAndFindByCampaign() {
        CommentRepository repo = new InMemoryCommentRepository();
        Comment c1 = new Comment("camp1", "userA", "Great job!");
        repo.save(c1);

        List<Comment> comments = repo.findByCampaignId("camp1");
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals("Great job!", comments.get(0).getText());
    }
}

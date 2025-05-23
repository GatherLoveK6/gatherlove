package k6.gatherlove.donation.repository;

import k6.gatherlove.donation.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repo;

    @Test
    void saveAndFindByCampaign() {
        // given
        Comment c1 = new Comment("camp1", "userA", "Great job!");
        repo.save(c1);

        // when
        List<Comment> comments = repo.findByCampaignId("camp1");

        // then
        assertThat(comments)
                .hasSize(1)
                .first()
                .extracting(Comment::getText)
                .isEqualTo("Great job!");
    }
}

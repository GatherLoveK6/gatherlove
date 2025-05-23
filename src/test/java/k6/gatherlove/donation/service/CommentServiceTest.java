package k6.gatherlove.donation.service;

import k6.gatherlove.donation.model.Comment;
import k6.gatherlove.donation.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentServiceTest {

    @Autowired
    private CommentRepository repo;

    private CommentService svc;
    private RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        svc = new CommentServiceImpl(repo, restTemplateBuilder);
        ReflectionTestUtils.setField(svc, "notificationBaseUrl", "");
    }

    @Test
    void testAddAndListComments() {
        svc.addComment("campX", "userZ", "Love this!");
        List<Comment> comments = svc.listComments("campX");

        assertEquals(1, comments.size());
        assertEquals("Love this!", comments.get(0).getText());
        assertEquals("userZ",      comments.get(0).getUserId());
    }
}

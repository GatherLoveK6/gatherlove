package k6.gatherlove.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import k6.gatherlove.auth.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.mock.web.*;

import java.io.IOException;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

class JwtAuthenticationFilterTest {

    @Mock JwtUtil jwtUtil;
    @InjectMocks JwtAuthenticationFilter filter;

    MockHttpServletRequest req;
    MockHttpServletResponse res;
    FilterChain chain = Mockito.mock(FilterChain.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // clear any previous auth:
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
    }

    @Test
    void whenNoCookie_thenChainContinuesAndNoAuthSet() throws ServletException, IOException {
        filter.doFilterInternal(req, res, chain);
        then(chain).should().doFilter(req, res);
        assertThat(org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void whenCookieButInvalidToken_thenNoAuth() throws ServletException, IOException {
        req.setCookies(new Cookie("JWT", "bad"));
        given(jwtUtil.validateToken("bad")).willReturn(false);

        filter.doFilterInternal(req, res, chain);

        then(jwtUtil).should().validateToken("bad");
        then(chain).should().doFilter(req, res);
        assertThat(org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void whenValidToken_thenAuthenticationSet() throws ServletException, IOException {
        req.setCookies(new Cookie("JWT", "good"));
        var dummyAuth = new org.springframework.security.authentication
                .UsernamePasswordAuthenticationToken("user", null);
        given(jwtUtil.validateToken("good")).willReturn(true);
        given(jwtUtil.getAuthentication("good")).willReturn(dummyAuth);

        filter.doFilterInternal(req, res, chain);

        then(jwtUtil).should().validateToken("good");
        then(jwtUtil).should().getAuthentication("good");
        then(chain).should().doFilter(req, res);

        var auth = org.springframework.security.core.context
                .SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isSameAs(dummyAuth);
    }
}

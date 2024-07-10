package mtn.momo.contract.repayment.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mtn.momo.contract.repayment.service.impl.UserDetailsServiceImpl;
import mtn.momo.contract.repayment.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;

public class JwtRequestFilterTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdG4ubW9tb0BtdG4uY28uemEiLCJleHAiOjE3MDMxOTkzMDYsImlhdCI6MTcwMzE2MzMwNn0.7yZFmwx1ML5f-ZATqEVb2c2Y5rxKDzMGRU6iG5PWsO8";
    String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdG4ubW9tb0BtdG4uY28uemEiLCJleHAiOjE3MDMxOTkzMDYsImlhdCI6MTcwMzE2MzMwNn0.invalidsignature";
    String header = "Bearer " + validToken;
    String invalidHeader = "Bearer " + invalidToken;
    String username = "mtn.momo@mtn.co.za";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoFilterInternal_WithValidJwt() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(validToken, userDetails);
        verify(filterChain).doFilter(request, response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        assertEquals("Authentication object should be set in the context",
                SecurityContextHolder.getContext().getAuthentication(), authentication);
    }

    @Test
    public void testDoFilterInternal_WithInvalidJwt() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(invalidHeader);
        when(jwtUtil.extractUsername(invalidToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(invalidToken, userDetails)).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(invalidToken, userDetails);
        verify(filterChain).doFilter(request, response);

        assertEquals("Authentication object should be null in the context",
                SecurityContextHolder.getContext().getAuthentication(), null);
    }

    @Test
    public void testDoFilterInternal_WithNoJwt() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertEquals("Authentication object should be null in the context",
                SecurityContextHolder.getContext().getAuthentication(), null);
    }

    @Test
    public void testDoFilterInternal_WithInvalidHeaderFormat() throws ServletException, IOException {
        String header = "Invalid header format";

        when(request.getHeader("Authorization")).thenReturn(header);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertEquals("Authentication object should be null in the context",
                SecurityContextHolder.getContext().getAuthentication(), null);
    }

    @Test
    public void testDoFilterInternal_WithException() throws ServletException, IOException {
        String header = "Bearer " + validToken;
        String username = "mtn.momo@mtn.co.za";

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenThrow(new RuntimeException("User not found"));

        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("Unauthorized: Authentication token was invalid or expired.");
    }
}

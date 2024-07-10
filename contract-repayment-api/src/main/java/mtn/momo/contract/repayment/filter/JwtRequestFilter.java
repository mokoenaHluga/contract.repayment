package mtn.momo.contract.repayment.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtn.momo.contract.repayment.service.impl.UserDetailsServiceImpl;
import mtn.momo.contract.repayment.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Filters each incoming HTTP request, extracts and validates the JWT, and sets the authentication in the context.
     *
     * @param request     the incoming HttpServletRequest
     * @param response    the outgoing HttpServletResponse
     * @param filterChain the filter chain to pass the request and response after filtering
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        // Extract authorization header and attempt to retrieve the JWT token
        final String header = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if the Authorization header is present and if it contains a bearer token
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            username = jwtUtil.extractUsername(jwt);
            log.info("JWT extracted");
        } else {
            log.info("No JWT found in header or header format incorrect");
        }

        // Validate the token and set the user in the SecurityContext
        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Security exception for user {} - {}", username, e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Authentication token was invalid or expired.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

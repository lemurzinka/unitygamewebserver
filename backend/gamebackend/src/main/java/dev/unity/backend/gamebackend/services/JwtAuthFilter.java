package dev.unity.backend.gamebackend.services;

import dev.unity.backend.gamebackend.repository.UserRepository;
import dev.unity.backend.gamebackend.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        logger.info("JWT Filter triggered for URI: {}", request.getRequestURI());

      
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("No token provided — skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        logger.info("Extracted token: {}", token);

        String email = jwtService.extractUsername(token);
        logger.info("Extracted email from token: {}", email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
            if (user == null) {
                logger.warn("No user found with email: {}", email);
            } else {
                logger.info("Found user: {} (id={})", user.getUsername(), user.getId());
                if (jwtService.validateToken(token, user)) {
                    logger.info("🔒 Token is valid for user {}", user.getUsername());
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, List.of());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Token is NOT valid for user {}", user.getUsername());
                }
            }
        } else {
            logger.warn("Email is null or authentication already exists");
        }

        filterChain.doFilter(request, response);
    }
}

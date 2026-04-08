package com.guvi.security;

import com.guvi.entity.User;
import com.guvi.repository.UserRepository;
import com.guvi.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component  // ✅ VERY IMPORTANT
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String path = request.getServletPath();

            // ✅ Skip auth APIs
            if (path.startsWith("/api/auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = getJwtFromRequest(request);
            System.out.println("JWT Token: " + jwt);
//            System.out.println("Username: " + username);
            System.out.println("Auth before: " + SecurityContextHolder.getContext().getAuthentication());

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                String username = tokenProvider.getUsernameFromToken(jwt);

                var optionalUser = userRepository.findByUsername(username);

                if (optionalUser.isPresent() &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    User user = optionalUser.get();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    Collections.emptyList() // ✅ avoid null authorities
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // ✅ SET AUTHENTICATION
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (Exception ex) {
            logger.error("JWT Authentication failed", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // ✅ no "-"
        }

        return null;
    }
}
package com.coachera.backend.security;

import com.coachera.backend.entity.AccessToken;
import com.coachera.backend.entity.User;
import com.coachera.backend.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AccessTokenRepository tokenRepo;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            AccessToken at = tokenRepo.findByToken(token).orElse(null);
            User userEntity = at.getUser();
            if (at != null && at.getExpiresAt().isAfter(LocalDateTime.now())) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(at.getUser().getUsername());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userEntity,
                        null,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}

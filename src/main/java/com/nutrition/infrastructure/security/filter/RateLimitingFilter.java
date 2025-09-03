package com.nutrition.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.rate-limit.login.attempts}")
    private int maxAttempts;

    @Value("${app.rate-limit.login.window}")
    private int windowSeconds;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!request.getRequestURI().equals("/api/v1/auth/login") ||
                !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIP = getClientIP(request);
        String key = "rate_limit:login:" + clientIP;

        try {
            String attempts = redisTemplate.opsForValue().get(key);
            int currentAttempts = attempts != null ? Integer.parseInt(attempts) : 0;

            if (currentAttempts >= maxAttempts) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"success\": false, \"message\": \"Muitas tentativas de login. Tente novamente em " +
                                windowSeconds / 60 + " minutos.\"}"
                );
                return;
            }

            filterChain.doFilter(request, response);

            if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                incrementAttempts(key);
            }

        } catch (Exception e) {
            log.error("Error in rate limiting filter: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private void incrementAttempts(String key) {
        try {
            Long current = redisTemplate.opsForValue().increment(key);
            if (current == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
            }
        } catch (Exception e) {
            log.error("Error incrementing rate limit counter: {}", e.getMessage());
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}

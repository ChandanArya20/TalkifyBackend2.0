package in.ineuron.security.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${ratelimit.bandwidth1.capacity}")
    private int bandwidth1Capacity;

    @Value("${ratelimit.bandwidth1.refill}")
    private int bandwidth1Refill;

    @Value("${ratelimit.bandwidth1.interval}")
    private int bandwidth1Interval;

    @Value("${ratelimit.bandwidth2.capacity}")
    private int bandwidth2Capacity;

    @Value("${ratelimit.bandwidth2.refill}")
    private int bandwidth2Refill;

    @Value("${ratelimit.bandwidth2.interval}")
    private int bandwidth2Interval;

    private Bucket bucket;

    @PostConstruct
    public void init() {
        // Create two bandwidth configurations
        Bandwidth limit1 = Bandwidth.classic(
                bandwidth1Capacity,
                Refill.intervally(bandwidth1Refill, Duration.ofSeconds(bandwidth1Interval))
        );

        Bandwidth limit2 = Bandwidth.classic(
                bandwidth2Capacity,
                Refill.intervally(bandwidth2Refill, Duration.ofSeconds(bandwidth2Interval))
        );

        // Build the bucket with the defined bandwidths
        this.bucket = Bucket4j.builder()
                .addLimit(limit1)
                .addLimit(limit2)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Consume a token for each request
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response); // Allow the request to proceed
        } else {
            // Reject with 429 Too Many Requests
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }
}

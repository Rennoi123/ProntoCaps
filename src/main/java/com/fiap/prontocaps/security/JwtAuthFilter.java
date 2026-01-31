package com.fiap.prontocaps.security;

import com.fiap.prontocaps.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth == null || !auth.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }

    String token = auth.substring("Bearer ".length()).trim();

    try {
      String username = jwtService.extractUsername(token);
      String role = jwtService.extractRole(token);

      if (username == null || role == null) {
        chain.doFilter(req, res);
        return;
      }

      var userOpt = userRepository.findByUsernameAndActiveTrue(username);
      if (userOpt.isEmpty()) {
        chain.doFilter(req, res);
        return;
      }

      var authToken = new UsernamePasswordAuthenticationToken(
          username,
          null,
          List.of(new SimpleGrantedAuthority(role))
      );
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (Exception ignored) {
    }

    chain.doFilter(req, res);
  }
}

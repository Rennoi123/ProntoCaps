package com.fiap.prontocaps.auth;

import com.fiap.prontocaps.auth.dto.LoginRequest;
import com.fiap.prontocaps.auth.dto.RegisterRequest;
import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.security.JwtTokenProvider;
import com.fiap.prontocaps.user.UserEntity;
import com.fiap.prontocaps.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthService(UserRepository userRepository, PasswordEncoder encoder, JwtTokenProvider jwtTokenProvider) {
    this.userRepository = userRepository;
    this.encoder = encoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public void register(RegisterRequest req) {
    if (userRepository.existsByEmail(req.email())) {
      throw new BusinessException("Email ja cadastrado");
    }
    UserEntity u = new UserEntity();
    u.setEmail(req.email());
    u.setPassword(encoder.encode(req.password()));
    u.setName(req.name());
    u.setRoles(req.roles());
    u.setActive(true);
    userRepository.save(u);
  }

  public String login(LoginRequest req) {
    var user = userRepository.findByEmail(req.email())
            .orElseThrow(() -> new BusinessException("Credenciais invalidas"));

    if (!encoder.matches(req.password(), user.getPassword())) {
      throw new BusinessException("Credenciais invalidas");
    }

    return jwtTokenProvider.generateToken(user);
  }
}
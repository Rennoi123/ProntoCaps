package com.fiap.prontocaps.auth;

import com.fiap.prontocaps.auth.dto.LoginRequest;
import com.fiap.prontocaps.auth.dto.RegisterRequest;
import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.security.JwtService;
import com.fiap.prontocaps.user.UserEntity;
import com.fiap.prontocaps.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder encoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  public void register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.username())) {
      throw new BusinessException("Username ja existe");
    }
    UserEntity u = new UserEntity();
    u.setName(req.username());
    u.setPassword(encoder.encode(req.password()));
    u.setRoles(req.roles());
    u.setActive(true);
    userRepository.save(u);
  }

  public String login(LoginRequest req) {
    var user = userRepository.findByUsernameAndActiveTrue(req.username())
        .orElseThrow(() -> new BusinessException("Credenciais invalidas"));

    if (!encoder.matches(req.password(), user.getPassword())) {
      throw new BusinessException("Credenciais invalidas");
    }

    return jwtService.generateToken(user.getUsername(), user.getRoles());
  }
}

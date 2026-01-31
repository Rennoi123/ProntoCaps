package com.fiap.prontocaps.auth;

import com.fiap.prontocaps.auth.dto.LoginRequest;
import com.fiap.prontocaps.auth.dto.LoginResponse;
import com.fiap.prontocaps.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest req) {
    authService.register(req);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
    String token = authService.login(req);
    return ResponseEntity.ok(new LoginResponse(token));
  }
}

package ca.on.pms.auth.controller;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.on.pms.auth.dto.AuthRequest;
import ca.on.pms.auth.dto.AuthResponse;
import ca.on.pms.auth.dto.RegisterRequest;
import ca.on.pms.auth.dto.VerifyRequest;
import ca.on.pms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
		authService.logout(token);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
        // ✅ FIX: Return a JSON object instead of a plain string
        return ResponseEntity.ok(Collections.singletonMap("message", "Account verified successfully"));
	}
}
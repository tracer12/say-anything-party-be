package com.test.say_anything_party.controller;

import com.test.say_anything_party.dto.LoginRequest;
import com.test.say_anything_party.dto.LoginResponse;
import com.test.say_anything_party.model.User;
import com.test.say_anything_party.repository.UserRepository;
import com.test.say_anything_party.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 이메일로 사용자 찾기
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        User user = userOptional.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // 로그인 성공 시 profileImage도 반환
        String profileImage = user.getProfileImage(); // 프로필 이미지 가져오기

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, profileImage));
    }
}

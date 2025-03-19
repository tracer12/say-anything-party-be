package com.test.say_anything_party.controller;

import com.test.say_anything_party.dto.UserResponse;
import com.test.say_anything_party.dto.PasswordRequest;
import com.test.say_anything_party.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<UserResponse> register(
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("passwordCheck") String passwordCheck,
            @RequestPart("nickname") String nickname,
            @RequestPart(value = "profile_image", required = false) MultipartFile profile_image) {
        UserResponse response = userService.registerUser(email, password, passwordCheck, nickname, profile_image);
        return ResponseEntity.ok(response);
    }

    // 유저 정보 받아오기
    @GetMapping
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7); // Bearer 토큰 제거
        try {
            UserResponse userResponse = userService.getUserInfo(token);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build(); // 403 Forbidden (권한 없음)
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (조회 실패)
        }
    }

    // 프로필 변경
    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestPart(value = "nickname", required = false) String nickname,
            @RequestPart(value = "profile_image", required = false) MultipartFile profileImage) {

        String token = authorization.substring(7); // Bearer 토큰 제거
        try {
            UserResponse updatedUser = userService.updateProfile(token, nickname, profileImage);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build(); // 403 Forbidden (권한 없음)
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (요청 오류)
        }
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PasswordRequest passwordRequest) {

        String token = authorization.substring(7); // Bearer 토큰 제거
        try {
            userService.updatePassword(token, passwordRequest.getPassword(), passwordRequest.getPasswordCheck());
            return ResponseEntity.noContent().build(); // 성공 시 응답 본문 없음
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (비밀번호 불일치 또는 오류 발생)
        }
    }

     // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7); // Bearer 토큰 제거
        try {
            userService.deleteUser(token);
            return ResponseEntity.noContent().build(); // 204 No Content (삭제 성공)
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build(); // 403 Forbidden (권한 없음)
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (삭제 실패)
        }
    }
}

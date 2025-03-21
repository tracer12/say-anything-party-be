package com.test.say_anything_party.service;

import com.test.say_anything_party.dto.UserResponse;
import com.test.say_anything_party.model.Post;
import com.test.say_anything_party.model.User;
import com.test.say_anything_party.repository.UserRepository;
import com.test.say_anything_party.repository.CommentRepository;
import com.test.say_anything_party.repository.PostRepository;
import com.test.say_anything_party.repository.LikeRepository;
import com.test.say_anything_party.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PostRepository postRepository,
                       CommentRepository commentRepository,
                       LikeRepository likeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // 회원가입
    public UserResponse registerUser(String email, String password, String passwordCheck, String nickname, MultipartFile profileImage) {
        String encodedPassword = passwordEncoder.encode(password);
        String profileImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = saveImage(profileImage);
        }

        User newUser = new User(email, encodedPassword, nickname, profileImageUrl);
        User savedUser = userRepository.save(newUser);

        return new UserResponse(savedUser.getUid(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getProfileImage());
    }

    public UserResponse getUserInfo(String token) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new UserResponse(user.getUid(), user.getEmail(), user.getNickname(), user.getProfileImage());
    }

    // 프로필 변경
    public UserResponse updateProfile(String token, String nickname, MultipartFile profileImage) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String newProfileImage = saveImage(profileImage);
            user.setProfileImage(newProfileImage);
        }

        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser.getUid(), updatedUser.getEmail(), updatedUser.getNickname(), updatedUser.getProfileImage());
    }

    public void updatePassword(String token, String password, String passwordCheck) {
        if (password == null || passwordCheck == null || !password.equals(passwordCheck)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화 후 저장
        userRepository.save(user);
    }

    @Transactional  //회원탈퇴, 트랜잭션 적용
    public void deleteUser(String token) {
        String email = jwtUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email);
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });

        // ✅ 1. 탈퇴할 사용자가 눌렀던 모든 좋아요 삭제
        likeRepository.deleteByUser(user);
        entityManager.flush();  // ✅ 강제 삭제 수행

        // ✅ 2. 탈퇴할 사용자가 작성한 모든 댓글을 삭제한다
        commentRepository.deleteByUser(user);
        entityManager.flush();  // ✅ 강제 삭제 수행

        // ✅ 3. 사용자가 작성한 모든 게시글을 찾는다
        List<Post> userPosts = postRepository.findByUser(user);

        if (!userPosts.isEmpty()) {
            // ✅ 4. 사용자가 작성한 모든 게시글의 좋아요 삭제
            userPosts.forEach(post -> {
                likeRepository.deleteByPost(post);
                commentRepository.deleteByPost(post);
                entityManager.flush();  // ✅ 강제 삭제 수행
            });

            // ✅ 5. 게시글을 삭제한다.
            postRepository.deleteAll(userPosts);
            entityManager.flush();  // ✅ 강제 삭제 수행
        }

        // ✅ 6. 최종적으로 사용자 계정 삭제
        userRepository.delete(user);
        entityManager.flush();  // ✅ 강제 삭제 수행

        System.out.println("✅ 회원 탈퇴 완료: " + email);
    }


    // 이미지 저장
    private String saveImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String originalFilename = imageFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        File saveFile = new File(uploadDir + uniqueFilename);

        try {
            imageFile.transferTo(saveFile);
            return "/uploads/" + uniqueFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

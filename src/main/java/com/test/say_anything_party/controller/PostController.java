package com.test.say_anything_party.controller;

import com.test.say_anything_party.dto.PostRequest;
import com.test.say_anything_party.dto.PostResponse;
import com.test.say_anything_party.dto.CommentRequest;
import com.test.say_anything_party.dto.CommentResponse;
import com.test.say_anything_party.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts") // 기본 api 주소
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // lists.js에서 모든 포스트 기본 정보 가져오기
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }

    // 게시글 작성 기능
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestHeader("Authorization") String authorization,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "postImage", required = false) MultipartFile postImage
    ) {
        String token = authorization.substring(7);
        try {
            PostResponse post = postService.createPost(token, title, content, postImage);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 게시글 상세정보 가져오기
    @GetMapping("/{pid}")
    public ResponseEntity<Map<String, Object>> getPostWithComments(@PathVariable Long pid) {
        Map<String, Object> postWithComments = postService.getPostWithComments(pid);
        if (postWithComments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postWithComments);
    }

    // 게시글 삭제
    @DeleteMapping("/{pid}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long pid,
            @RequestHeader("Authorization") String authorization) {

        String token = authorization.substring(7); // ✅ "Bearer " 제거

        try {
            postService.deletePost(pid, token);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("해당 게시글을 삭제할 권한이 없습니다.")) {
                return ResponseEntity.status(403).build(); // 403 Forbidden 반환
            }
            return ResponseEntity.notFound().build(); // 404 Not Found 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request 반환
        }
    }


    // 게시글 수정
    @PatchMapping("/{pid}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long pid,
            @RequestHeader("Authorization") String authorization,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "postImage", required = false) MultipartFile postImage
    ) {
        String token = authorization.substring(7);
        System.out.println("fucking image : " + postImage); // ㅣㅆ발 여기서 왜 갑자기 ?
        try {
            PostResponse updatedPost = postService.updatePost(pid, token, title, content, postImage);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("해당 게시글을 수정할 권한이 없습니다.")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 작성
    @PostMapping("/{pid}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long pid,
            @RequestHeader("Authorization") String authorization,
            @RequestBody CommentRequest request
    ) {
        String token = authorization.substring(7);

        try {
            CommentResponse comment = postService.createComment(pid, token, request);
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 수정
    @PatchMapping("/{pid}/comments/{cid}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long pid,
            @PathVariable Long cid,
            @RequestHeader("Authorization") String authorization,
            @RequestBody CommentRequest request
    ) {
        String token = authorization.substring(7);

        try {
            CommentResponse updatedComment = postService.updateComment(pid, cid, token, request);
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("해당 댓글을 수정할 권한이 없습니다.")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{pid}/comments/{cid}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long pid,
            @PathVariable Long cid,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.substring(7);

        try {
            postService.deleteComment(pid, cid, token);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("해당 댓글을 삭제할 권한이 없습니다.")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{pid}/like")
    public ResponseEntity<?> likePost(
            @PathVariable Long pid,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.substring(7);

        try {
            postService.likePost(pid, token);
            return ResponseEntity.ok().body("좋아요가 추가되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
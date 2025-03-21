package com.test.say_anything_party.service;

import com.test.say_anything_party.dto.PostRequest;
import com.test.say_anything_party.dto.PostResponse;
import com.test.say_anything_party.dto.CommentRequest;
import com.test.say_anything_party.dto.CommentResponse;
import com.test.say_anything_party.model.Post;
import com.test.say_anything_party.model.Comment;
import com.test.say_anything_party.model.User;
import com.test.say_anything_party.model.Like;
import com.test.say_anything_party.repository.PostRepository;
import com.test.say_anything_party.repository.CommentRepository;
import com.test.say_anything_party.repository.UserRepository;
import com.test.say_anything_party.repository.LikeRepository;
import com.test.say_anything_party.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository, LikeRepository likeRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.jwtUtil = jwtUtil;
    }

    // 모든 posts 정보를 가져옴(lists.js에 표시할 정보)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreateDateDesc()
                .stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    // 게시글 업로드
    public PostResponse createPost(String token, String title, String content, MultipartFile postImage) throws IOException {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String imageUrl = null;
        if (postImage != null && !postImage.isEmpty()) {
            imageUrl = saveImage(postImage);
        }


        Post post = new Post(
                title,
                imageUrl,
                content,
                0, 0, 0, new Date(), user
        );

        return new PostResponse(postRepository.save(post));
    }

    @Transactional
    public Map<String, Object> getPostWithComments(Long pid) {
        Optional<Post> postOptional = postRepository.findById(pid);

        if (postOptional.isEmpty()) {
            return Collections.emptyMap();
        }

        // ✅ 조회수 증가
        postRepository.incrementViews(pid);

        // ✅ DB에 즉시 반영
        entityManager.flush();

        // ✅ JPA 캐시 무효화 (1차 캐시 제거)
        entityManager.clear();

        // ✅ 조회수 반영된 상태로 다시 조회
        Post post = postRepository.findPostById(pid)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        List<CommentResponse> comments = commentRepository.findByPost(post)
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        System.out.println("📌 조회수 증가 후 views: " + post.getViews()); // ✅ 로그 확인
        response.put("post", new PostResponse(post));
        response.put("comments", comments);

        return response;
    }


    @Transactional
    public void deletePost(Long pid, String token) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Optional<Post> postOptional = postRepository.findById(pid);

        if (postOptional.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }

        Post post = postOptional.get();

        if (!post.getUser().getUid().equals(user.getUid())) {
            throw new RuntimeException("해당 게시글을 삭제할 권한이 없습니다.");
        }

        try {
            // ✅ 해당 게시글에 좋아요가 존재하면 삭제
            if (likeRepository.existsByPost(post)) {
                likeRepository.deleteByPost(post);
            }

            // ✅ 댓글 삭제
            commentRepository.deleteByPost(post);

            // ✅ 게시글 삭제
            postRepository.delete(post);

            System.out.println("✅ 게시글 삭제 성공: pid = " + pid);
        } catch (Exception e) {
            System.out.println("❌ 게시글 삭제 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("게시글 삭제 중 오류 발생");
        }
    }


    // 게시글 수정
    public PostResponse updatePost(Long pid, String token, String title, String content, MultipartFile postImage) throws IOException {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 본인 확인
        if (!post.getUser().getUid().equals(user.getUid())) {
            throw new RuntimeException("해당 게시글을 수정할 권한이 없습니다.");
        }

        // 수정할 데이터
        post.setTitle(title);
        post.setContent(content);

        // 이미지 변경 데이터
        if (postImage != null && !postImage.isEmpty()) {
            String newImageUrl = saveImage(postImage);
            System.out.println("why image : " + newImageUrl); // ㅣㅆ발 여기서 왜 갑자기 ?
            post.setPostImage(newImageUrl);
        } else {
            System.out.println("no image" + post.getPostImage());
        }

        return new PostResponse(postRepository.save(post));
    }

    // 댓글 작성 기능
    public CommentResponse createComment(Long pid, String token, CommentRequest request) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setCommentContent(request.getCommentContent());
        comment.setCreateDate(new Date());
        comment.setEdited(false);

        Comment savedComment = commentRepository.save(comment);

        // posts의 comments 증가
        commentRepository.incrementCommentCount(pid);

        return new CommentResponse(savedComment);
    }


    // 댓글 수정
    public CommentResponse updateComment(Long pid, Long cid, String token, CommentRequest request) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 본인확인
        if (!comment.getUser().getUid().equals(user.getUid())) {
            throw new RuntimeException("해당 댓글을 수정할 권한이 없습니다.");
        }
        
        comment.setCommentContent(request.getCommentContent());
        comment.setEdited(true); // 수정됨 표시

        return new CommentResponse(commentRepository.save(comment));
    }

    // 댓글 삭제
    public void deleteComment(Long pid, Long cid, String token) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        // 본인확인
        if (!comment.getUser().getUid().equals(user.getUid())) {
            throw new RuntimeException("해당 댓글을 삭제할 권한이 없습니다.");
        }
        
        commentRepository.delete(comment);
        //posts의 댓글 개수 감소
        commentRepository.decrementCommentCount(pid);
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

    public void likePost(Long pid, String token) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // ✅ 이미 좋아요 했는지 확인
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        Like like = new Like(user, post);
        likeRepository.save(like);

        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }
}
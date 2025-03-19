package com.test.say_anything_party.service;

import com.test.say_anything_party.dto.PostRequest;
import com.test.say_anything_party.dto.PostResponse;
import com.test.say_anything_party.dto.CommentRequest;
import com.test.say_anything_party.dto.CommentResponse;
import com.test.say_anything_party.model.Post;
import com.test.say_anything_party.model.Comment;
import com.test.say_anything_party.model.User;
import com.test.say_anything_party.repository.PostRepository;
import com.test.say_anything_party.repository.CommentRepository;
import com.test.say_anything_party.repository.UserRepository;
import com.test.say_anything_party.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
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

    // 게시글 상세 정보 가져오기(댓글이랑 같이)
    public Map<String, Object> getPostWithComments(Long pid) {
        Optional<Post> postOptional = postRepository.findById(pid);

        if (postOptional.isEmpty()) {
            return Collections.emptyMap();
        }

        Post post = postOptional.get();
        
        List<CommentResponse> comments = commentRepository.findByPost(post)
                .stream()
                .map(CommentResponse::new) // 댓글 작성자의 프로필 이미지도 포함
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("post", new PostResponse(post));
        response.put("comments", comments);

        return response;
    }


    // 게시글 삭제
    public void deletePost(Long pid) {
        Optional<Post> postOptional = postRepository.findById(pid);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            commentRepository.deleteByPost(post);
            postRepository.delete(post);
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
            post.setPostImage(newImageUrl);
        } else {
            System.out.println("📌 이미지 변경 없음, 기존 이미지 유지: " + post.getPostImage());
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
}
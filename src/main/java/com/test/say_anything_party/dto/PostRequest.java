package com.test.say_anything_party.dto;

import org.springframework.web.multipart.MultipartFile;

//게시글 작성 및 수정 dto
public class PostRequest {
    private String title;
    private String content;
    private MultipartFile postImage; // ✅ 이미지 파일 필드

    // 기본 생성자
    public PostRequest() {}

    // 생성자
    public PostRequest(String title, String content, MultipartFile postImage) {
        this.title = title;
        this.content = content;
        this.postImage = postImage;
    }

    // Getter & Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MultipartFile getPostImage() { return postImage; }
    public void setPostImage(MultipartFile postImage) { this.postImage = postImage; }
}

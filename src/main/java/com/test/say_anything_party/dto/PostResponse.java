package com.test.say_anything_party.dto;

import com.test.say_anything_party.model.Post;

import java.util.Date;

// 서버 -> 클라이언트 게시글 전송 dto
public class PostResponse {
    private Long pid;
    private String title;
    private String content;
    private String postImage;
    private int likes;
    private int comments;
    private int views;
    private Date createDate;
    private String nickname; // 작성자 닉네임
    private String profileImage; // 작성자 프로필 이미지

    // 생성자 (Post 객체를 받아서 DTO로 변환)
    public PostResponse(Post post) {
        this.pid = post.getPid();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.postImage = post.getPostImage();
        this.likes = post.getLikes();
        this.comments = post.getComments();
        this.views = post.getViews();
        this.createDate = post.getCreateDate();
        this.nickname = post.getUser().getNickname();
        this.profileImage = post.getUser().getProfileImage();
    }

    public Long getPid() { return pid; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getPostImage() { return postImage; }
    public int getLikes() { return likes; }
    public int getComments() { return comments; }
    public int getViews() { return views; }
    public Date getCreateDate() { return createDate; }
    public String getNickname() { return nickname; }
    public String getProfileImage() { return profileImage; }
}
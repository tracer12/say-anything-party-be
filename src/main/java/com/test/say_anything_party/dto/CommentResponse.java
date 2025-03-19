package com.test.say_anything_party.dto;

import com.test.say_anything_party.model.Comment;

import java.util.Date;

// 서버에서 프론트엔드로 댓글을 전달하는 객체
public class CommentResponse {
    private Long cid;
    private String commentContent;
    private Date createDate;
    private boolean edited;
    private Long pid;
    private Long uid;
    private String nickname; // ✅ 닉네임 추가
    private String profileImage; // ✅ 프로필 이미지 추가

    // 생성자 (Comment → DTO 변환)
    public CommentResponse(Comment comment) {
        this.cid = comment.getCid();
        this.commentContent = comment.getCommentContent();
        this.createDate = comment.getCreateDate();
        this.edited = comment.isEdited();
        this.pid = comment.getPost().getPid();
        this.uid = comment.getUser().getUid();
        this.nickname = comment.getUser().getNickname(); // ✅ 닉네임 추가
        this.profileImage = comment.getUser().getProfileImage(); // ✅ 프로필 이미지 추가
    }

    // Getters
    public Long getCid() { return cid; }
    public String getCommentContent() { return commentContent; }
    public Date getCreateDate() { return createDate; }
    public boolean isEdited() { return edited; }
    public Long getPid() { return pid; }
    public Long getUid() { return uid; }
    public String getNickname() { return nickname; } // ✅ 닉네임 Getter 추가
    public String getProfileImage() { return profileImage; } // ✅ 프로필 이미지 Getter 추가
}

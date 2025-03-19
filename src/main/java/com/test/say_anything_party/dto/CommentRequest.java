package com.test.say_anything_party.dto;

//클라이언트에서 서버로 요청 데이트를 전달하기 위한 클래스
public class CommentRequest {

    //사용자가 입력한 댓글 내용
    private String commentContent;

    //기본 생성자
    public CommentRequest() {}

    // 댓글 내용을 받아서 객체를 생성함
    public CommentRequest(String commentContent) {
        this.commentContent = commentContent;
    }

    // 댓글 내용을 가져옴
    public String getCommentContent() {
        return commentContent;
    }

    //댓글 내용을 설정
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}

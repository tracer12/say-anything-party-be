package com.test.say_anything_party.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cid;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // 타임스탬프
    private Date createDate;

    @Column(nullable = false)
    private boolean edited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", referencedColumnName = "pid", nullable = false, updatable = false)
    private Post post; // 댓글이 속한 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, updatable = false)
    private User user; // 댓글 작성자

    public Comment() {}

    public Comment(String commentContent, Date createDate, boolean edited, Post post, User user) {
        this.commentContent = commentContent;
        this.createDate = createDate;
        this.edited = edited;
        this.post = post;
        this.user = user;
    }

    public Long getCid() { return cid; }

    public String getCommentContent() { return commentContent; }
    public void setCommentContent(String commentContent) { this.commentContent = commentContent; }

    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date createDate) { this.createDate = createDate; }

    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getPid() { return post != null ? post.getPid() : null; }
    public Long getUid() { return user != null ? user.getUid() : null; }
}

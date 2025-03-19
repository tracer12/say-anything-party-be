package com.test.say_anything_party.model;

import jakarta.persistence.*;
import java.util.Date;

// post 엔티티임을 선언
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "post_image")
    private String postImage;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int likes;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int comments;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int views;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
    private User user;

    protected Post() {}

    public Post(String title, String postImage, String content, int likes, int comments, int views, Date createDate, User user) {
        this.title = title;
        this.postImage = postImage;  // ✅ postImage 값 저장
        this.content = content;
        this.likes = likes;
        this.comments = comments;
        this.views = views;
        this.createDate = createDate;
        this.user = user;
    }

    public Long getPid() { return pid; }
    public void setPid(Long pid) { this.pid = pid; }

    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date createDate) { this.createDate = createDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPostImage() { return postImage; }  // ✅ 변수명과 일치
    public void setPostImage(String postImage) { this.postImage = postImage; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

package com.test.say_anything_party.model;

import jakarta.persistence.*;

@Entity
@Table(name = "likes", uniqueConstraints = {@UniqueConstraint(columnNames = {"uid", "pid"})})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", nullable = false)
    private Post post;

    protected Like() {}

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public Long getLid() { return lid; }
    public User getUser() { return user; }
    public Post getPost() { return post; }
}
